package it.unibo.pcd.assignment2.reactive.controller.impl;

import hu.akarnokd.rxjava3.operators.FlowableTransformers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.processors.PublishProcessor;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.unibo.pcd.assignment2.reactive.controller.Controller;
import it.unibo.pcd.assignment2.reactive.controller.tasks.ComputationEndTask;
import it.unibo.pcd.assignment2.reactive.controller.tasks.UpdateConsumerTask;
import it.unibo.pcd.assignment2.reactive.model.tasks.DocumentLoaderTask;
import it.unibo.pcd.assignment2.reactive.model.tasks.DocumentSplitterTask;
import it.unibo.pcd.assignment2.reactive.model.tasks.PathLoaderTask;
import it.unibo.pcd.assignment2.reactive.model.tasks.UpdateProjectorTask;
import it.unibo.pcd.assignment2.reactive.model.tasks.WordCounterTask;
import it.unibo.pcd.assignment2.reactive.model.tasks.WordAggregatorTask;
import it.unibo.pcd.assignment2.reactive.view.View;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An implementation of the {@link Controller} interface.
 */
public class ControllerImpl implements Controller {
    private static final int MILLIS_BETWEEN_FRAMES = Math.round(1000.0f / 60.0f);

    private final View view;
    private final Map<Class<?>, PublishProcessor<Boolean>> valves;
    private Optional<Disposable> currentFlowable;


    /**
     * Default constructor.
     * @param view the View component to be notified by this Controller instance
     */
    public ControllerImpl(final View view) {
        this.view = Objects.requireNonNull(view);
        this.currentFlowable = Optional.empty();
        this.valves = Stream.of(PathLoaderTask.class,
                                DocumentLoaderTask.class,
                                DocumentSplitterTask.class,
                                WordCounterTask.class,
                                WordAggregatorTask.class,
                                UpdateProjectorTask.class)
                            .collect(Collectors.toMap(Function.identity(), v -> PublishProcessor.create()));
    }

    @Override
    public void launch(final Path filesDirectory, final Path stopwordsFile, final int wordsNumber) {
        this.currentFlowable.ifPresent(Disposable::dispose);
        this.currentFlowable = Optional.of(
            Flowable.switchOnNext(
                Flowable.zip(
                    Flowable.just(filesDirectory)
                            .observeOn(Schedulers.io())
                            .flatMapStream(new PathLoaderTask())
                            .compose(FlowableTransformers.valve(this.valves.get(PathLoaderTask.class)))
                            .map(new DocumentLoaderTask())
                            .compose(FlowableTransformers.valve(this.valves.get(DocumentLoaderTask.class)))
                            .flatMapStream(new DocumentSplitterTask())
                            .compose(FlowableTransformers.valve(this.valves.get(DocumentSplitterTask.class))),
                    Flowable.just(stopwordsFile)
                            .observeOn(Schedulers.io())
                            .map(Files::readAllLines)
                            .observeOn(Schedulers.computation())
                            .map(HashSet::new)
                            .repeat(),
                    new WordCounterTask()
                )
                .compose(FlowableTransformers.valve(this.valves.get(WordCounterTask.class)))
                .scan(new WordAggregatorTask())
                .compose(FlowableTransformers.valve(this.valves.get(WordAggregatorTask.class)))
                .observeOn(Schedulers.single())
                .window(MILLIS_BETWEEN_FRAMES, TimeUnit.MILLISECONDS)
            )
            .map(new UpdateProjectorTask(wordsNumber))
            .compose(FlowableTransformers.valve(this.valves.get(UpdateProjectorTask.class)))
            .subscribeOn(Schedulers.computation())
            .subscribe(new UpdateConsumerTask(this.view), System.err::println, new ComputationEndTask(this.view)));
    }

    @Override
    public void suspend() {
        this.changeValveState(false);
    }

    @Override
    public void resume() {
        this.changeValveState(true);
    }

    @Override
    public void exit() {
        System.exit(0);
    }

    /*
     * Changes the state of the Flowable used as a valve deciding whether or not to resume the flow given the boolean parameter
     * passed.
     */
    private void changeValveState(final boolean resume) {
        ForkJoinPool.commonPool().submit(() -> this.valves.values().forEach(v -> v.onNext(resume)));
    }
}
