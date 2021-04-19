package it.unibo.pcd.assignment2.executors.controller.tasks;

import it.unibo.pcd.assignment2.executors.model.entities.Update;
import it.unibo.pcd.assignment2.executors.model.pipes.WordCounter;
import it.unibo.pcd.assignment2.executors.view.View;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

/**
 * A task for collecting the results of the computation, the
 * {@link Update}s, and publish them to the View component.
 */
public class UpdateSinkTask implements Runnable {
    private final ScheduledExecutorService executor;
    private final WordCounter wordCounter;
    private final View view;

    /**
     * Default constructor.
     * @param executor the executor used to execute this task
     * @param wordCounter the pipe from which getting the {@link Update}s
     * @param view the View component this task should publish its {@link Update}s onto
     */
    public UpdateSinkTask(final ScheduledExecutorService executor, final WordCounter wordCounter, final View view) {
        this.executor = Objects.requireNonNull(executor);
        this.wordCounter = Objects.requireNonNull(wordCounter);
        this.view = Objects.requireNonNull(view);
    }

    @Override
    public void run() {
        final Optional<Update> optUpdate = this.wordCounter.dequeue();
        if (optUpdate.isPresent()) {
            final Update update = optUpdate.get();
            this.view.displayProgress(update.getFrequencies(), update.getProcessedWords());
        } else if (this.wordCounter.isClosed()) {
            this.view.displayCompletion();
            this.executor.shutdown();
        }
    }
}
