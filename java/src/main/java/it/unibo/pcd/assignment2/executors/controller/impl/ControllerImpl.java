package it.unibo.pcd.assignment2.executors.controller.impl;

import it.unibo.pcd.assignment2.executors.controller.Controller;
import it.unibo.pcd.assignment2.executors.controller.executor.SuspendableForkJoinPool;
import it.unibo.pcd.assignment2.executors.controller.tasks.UpdateConsumerTask;
import it.unibo.pcd.assignment2.executors.model.entities.impl.SourcePathsImpl;
import it.unibo.pcd.assignment2.executors.model.pipes.WordCounter;
import it.unibo.pcd.assignment2.executors.model.pipes.impl.WordCounterImpl;
import it.unibo.pcd.assignment2.executors.model.shared.CompletedFlag;
import it.unibo.pcd.assignment2.executors.model.shared.SuspendedFlag;
import it.unibo.pcd.assignment2.executors.model.shared.impl.CompletedFlagImpl;
import it.unibo.pcd.assignment2.executors.model.shared.impl.SuspendedFlagImpl;
import it.unibo.pcd.assignment2.executors.model.tasks.DocumentLoaderTask;
import it.unibo.pcd.assignment2.executors.model.tasks.DocumentSplitterTask;
import it.unibo.pcd.assignment2.executors.model.tasks.PathLoaderTask;
import it.unibo.pcd.assignment2.executors.model.tasks.WordCounterTask;
import it.unibo.pcd.assignment2.executors.model.tasks.WordEnqueueerTask;
import it.unibo.pcd.assignment2.executors.view.View;

import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * An implementation of the {@link Controller} interface.
 */
public class ControllerImpl implements Controller {
    private static final int TOTAL_THREADS = Math.round(Runtime.getRuntime().availableProcessors() * 1.0f * (1 + 1.093f));
    private static final int MILLIS_BETWEEN_FRAMES = Math.round(1000.0f / 60.0f);
    public static final int INITIAL_DELAY_MILLIS = 0;

    private final View view;
    private final Executor executor;
    private final SuspendedFlag suspendedFlag;

    /**
     * Default constructor.
     * @param view the View component to be notified by this Controller instance
     */
    public ControllerImpl(final View view) {
        this.view = Objects.requireNonNull(view);
        this.suspendedFlag = new SuspendedFlagImpl();
        this.executor = new SuspendableForkJoinPool(TOTAL_THREADS - 1, this.suspendedFlag);
    }

    @Override
    public void launch(final Path filesDirectory, final Path stopwordsFile, final int wordsNumber) {
        final ScheduledExecutorService timerExecutor = Executors.newSingleThreadScheduledExecutor();
        final CompletedFlag completedFlag = new CompletedFlagImpl();
        final WordCounter wordCounter = new WordCounterImpl(wordsNumber);
        CompletableFuture
            .completedFuture(new SourcePathsImpl(filesDirectory, stopwordsFile))
            .thenApplyAsync(new PathLoaderTask(), this.executor)
            .thenComposeAsync(
                d -> CompletableFuture.allOf(
                    d.getPDFFiles()
                     .stream()
                     .map(p -> CompletableFuture
                         .completedFuture(p)
                         .thenApplyAsync(new DocumentLoaderTask(), this.executor)
                         .thenApplyAsync(new DocumentSplitterTask(), this.executor)
                         .thenComposeAsync(
                             pgs -> CompletableFuture.allOf(
                                 pgs.stream()
                                    .map(pg -> CompletableFuture
                                        .completedFuture(pg)
                                        .thenApplyAsync(new WordCounterTask(d.getStopwords()), this.executor)
                                        .thenAcceptAsync(new WordEnqueueerTask(wordCounter), this.executor))
                                    .toArray(CompletableFuture[]::new)),
                             this.executor
                         ))
                     .toArray(CompletableFuture[]::new)),
                this.executor
            )
            .thenRunAsync(completedFlag::setComplete, this.executor)
            .whenCompleteAsync((unused, t) -> this.view.displayError(t.getMessage()), this.executor);
        timerExecutor.scheduleAtFixedRate(new UpdateConsumerTask(timerExecutor, wordCounter, completedFlag, this.view),
                                          INITIAL_DELAY_MILLIS,
                                          MILLIS_BETWEEN_FRAMES,
                                          TimeUnit.MILLISECONDS);
    }

    @Override
    public void suspend() {
        this.suspendedFlag.setSuspended();
    }

    @Override
    public void resume() {
        this.suspendedFlag.setRunning();
    }

    @Override
    public void exit() {
        System.exit(0);
    }
}
