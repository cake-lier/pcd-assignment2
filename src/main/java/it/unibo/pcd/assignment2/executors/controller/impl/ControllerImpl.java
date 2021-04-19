package it.unibo.pcd.assignment2.executors.controller.impl;

import it.unibo.pcd.assignment2.executors.controller.Controller;
import it.unibo.pcd.assignment2.executors.controller.executor.PausableExecutor;
import it.unibo.pcd.assignment2.executors.controller.executor.PausableExecutorImpl;
import it.unibo.pcd.assignment2.executors.controller.tasks.UpdateSinkTask;
import it.unibo.pcd.assignment2.executors.model.entities.impl.SourcePathsImpl;
import it.unibo.pcd.assignment2.executors.model.pipes.WordCounter;
import it.unibo.pcd.assignment2.executors.model.pipes.impl.WordCounterImpl;
import it.unibo.pcd.assignment2.executors.model.tasks.DocumentLoaderTask;
import it.unibo.pcd.assignment2.executors.model.tasks.DocumentSplitterTask;
import it.unibo.pcd.assignment2.executors.model.tasks.PathLoaderTask;
import it.unibo.pcd.assignment2.executors.model.tasks.WordCountingTask;
import it.unibo.pcd.assignment2.executors.model.tasks.WordEnqueueingTask;
import it.unibo.pcd.assignment2.executors.view.View;

import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * An implementation of the {@link Controller} interface.
 */
public class ControllerImpl implements Controller {
    private static final int TOTAL_THREADS = Math.round(Runtime.getRuntime().availableProcessors() * 1.0f * (1 + 1.093f));
    private static final int MILLIS_BETWEEN_FRAMES = Math.round(1000.0f / 60.0f);

    private final View view;
    private final PausableExecutor taskExecutor;

    /**
     * Default constructor.
     * @param view the View component to be notified by this Controller instance
     */
    public ControllerImpl(final View view) {
        this.view = Objects.requireNonNull(view);
        this.taskExecutor = new PausableExecutorImpl(TOTAL_THREADS - 1);
    }

    @Override
    public void launch(final Path filesDirectory, final Path stopwordsFile, final int wordsNumber) {
        final ScheduledExecutorService timerExecutor = Executors.newSingleThreadScheduledExecutor();
        final WordCounter wordCounter = new WordCounterImpl(wordsNumber);
        CompletableFuture
            .supplyAsync(() -> new SourcePathsImpl(filesDirectory, stopwordsFile), this.taskExecutor)
            .thenApplyAsync(new PathLoaderTask(), this.taskExecutor)
            .thenComposeAsync(
                d -> CompletableFuture.allOf(
                    d.getPDFFiles()
                     .stream()
                     .map(p -> CompletableFuture
                         .supplyAsync(() -> p, this.taskExecutor)
                         .thenApplyAsync(new DocumentLoaderTask(), this.taskExecutor)
                         .thenApplyAsync(new DocumentSplitterTask(), this.taskExecutor)
                         .thenComposeAsync(
                             pgs -> CompletableFuture.allOf(
                                 pgs.stream()
                                    .map(pg -> CompletableFuture
                                        .supplyAsync(() -> pg, this.taskExecutor)
                                        .thenApplyAsync(new WordCountingTask(d.getStopwords()), this.taskExecutor)
                                        .thenAcceptAsync(new WordEnqueueingTask(wordCounter), this.taskExecutor))
                                    .toArray(CompletableFuture[]::new)),
                             this.taskExecutor
                         ))
                     .toArray(CompletableFuture[]::new)),
                this.taskExecutor
            )
            .thenRunAsync(wordCounter::close, this.taskExecutor)
            .whenCompleteAsync((unused, t) -> this.view.displayError(t.getMessage()), this.taskExecutor);
        timerExecutor.scheduleAtFixedRate(new UpdateSinkTask(timerExecutor, wordCounter, this.view),
                                          0,
                                          MILLIS_BETWEEN_FRAMES,
                                          TimeUnit.MILLISECONDS);
    }

    @Override
    public void suspend() {
        this.taskExecutor.pause();
    }

    @Override
    public void resume() {
        this.taskExecutor.resume();
    }

    @Override
    public void exit() {
        System.exit(0);
    }
}
