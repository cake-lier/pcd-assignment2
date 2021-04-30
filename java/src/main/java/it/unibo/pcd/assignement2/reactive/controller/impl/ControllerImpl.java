package it.unibo.pcd.assignement2.reactive.controller.impl;

import io.reactivex.rxjava3.core.FlowableConverter;
import io.reactivex.rxjava3.core.FlowableTransformer;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.processors.PublishProcessor;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import it.unibo.pcd.assignement2.reactive.controller.Controller;
import it.unibo.pcd.assignement2.reactive.controller.executor.PausableExecutor;
import it.unibo.pcd.assignement2.reactive.controller.executor.PausableExecutors;
import it.unibo.pcd.assignement2.reactive.controller.executor.SuspendableForkJoinPool;
import it.unibo.pcd.assignement2.reactive.controller.tasks.ComputationEndTask;
import it.unibo.pcd.assignement2.reactive.controller.tasks.UpdateConsumerTask;
import it.unibo.pcd.assignement2.reactive.model.entities.impl.SourcePathsImpl;
import it.unibo.pcd.assignement2.reactive.model.pipes.WordCounter;
import it.unibo.pcd.assignement2.reactive.model.pipes.impl.WordCounterImpl;
import it.unibo.pcd.assignement2.reactive.model.shared.CompletedFlag;
import it.unibo.pcd.assignement2.reactive.model.shared.SuspendedFlag;
import it.unibo.pcd.assignement2.reactive.model.shared.impl.CompletedFlagImpl;
import it.unibo.pcd.assignement2.reactive.model.shared.impl.SuspendedFlagImpl;
import it.unibo.pcd.assignement2.reactive.view.View;
import it.unibo.pcd.assignement2.reactive.model.tasks.*;

import java.nio.file.Path;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.*;

/**
 * An implementation of the {@link Controller} interface.
 */
public class ControllerImpl implements Controller {
    private static final int TOTAL_THREADS = Math.round(Runtime.getRuntime().availableProcessors() * 1.0f * (1 + 1.093f));
    private static final int MILLIS_BETWEEN_FRAMES = Math.round(1000.0f / 60.0f);

    private final List<PausableExecutor> executorList;
    private final View view;
    private final PausableExecutor ioExecutor;
    private final PausableExecutor computationExecutor;
    PublishProcessor<Boolean> valve = PublishProcessor.create();
    SuspendedFlag flag = new SuspendedFlagImpl();
    SuspendableForkJoinPool executor = new SuspendableForkJoinPool(TOTAL_THREADS,flag);


    /**
     * Default constructor.
     * @param view the View component to be notified by this Controller instance
     */
    public ControllerImpl(final View view) {
        this.ioExecutor = PausableExecutors.cachedThreadPool(TOTAL_THREADS);
        this.computationExecutor = PausableExecutors.fixedThreadPool(TOTAL_THREADS);
        this.executorList = Arrays.asList(this.ioExecutor, this.computationExecutor);
        this.view = Objects.requireNonNull(view);
    }

    @Override
    public void launch(final Path filesDirectory, final Path stopwordsFile, final int wordsNumber) {
        final WordCounter wordCounter = new WordCounterImpl(wordsNumber);


        Observable.just(new SourcePathsImpl(filesDirectory,stopwordsFile))
                .observeOn(Schedulers.from(this.ioExecutor))
                .map(new PathLoaderTask())
                .observeOn(Schedulers.from(this.computationExecutor))// TODO is it necessary?
                .subscribe(d->Observable.fromStream(d.getPDFFiles().stream())
                        .observeOn(Schedulers.from(this.ioExecutor))
                        .map(new DocumentLoaderTask())
                        .observeOn(Schedulers.from(this.computationExecutor))
                        .map(new DocumentSplitterTask())
                        .flatMap(pgs->Observable.fromStream(pgs.stream()))
                        .map(new WordCountingTask(d.getStopwords()))
                        .map(new WordEnqueueingTask(wordCounter))
                        .sample(MILLIS_BETWEEN_FRAMES, TimeUnit.MILLISECONDS)
                        .subscribe(
                                new UpdateConsumerTask(this.view),
                                System.out::println,
                                new ComputationEndTask(this.view))
                );
    }

    @Override
    public void suspend() {
        //this.executorList.forEach(PausableExecutor::pause);
        //valve.onNext(false);
        this.flag.setSuspended();
    }

    @Override
    public void resume() {
        //this.executorList.forEach(PausableExecutor::resume);
        //valve.onNext(true);
        this.flag.setRunning();
    }

    @Override
    public void exit() {
        System.exit(0);
    }
}
