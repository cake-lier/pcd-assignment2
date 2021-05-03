package it.unibo.pcd.assignment2.executors.controller.tasks;

import it.unibo.pcd.assignment2.executors.model.entities.Update;
import it.unibo.pcd.assignment2.executors.model.pipes.WordCounter;
import it.unibo.pcd.assignment2.executors.model.shared.CompletedFlag;
import it.unibo.pcd.assignment2.executors.view.View;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

/**
 * A task for collecting the results of the word counting process, the {@link Update}s, and publish them on the {@link View}
 * component.
 */
public class UpdateConsumerTask implements Runnable {
    private final ScheduledExecutorService executor;
    private final WordCounter wordCounter;
    private final View view;
    private final CompletedFlag completedFlag;

    /**
     * Default constructor.
     * @param executor the executor used to execute this task
     * @param wordCounter the pipe from which getting the {@link Update}s
     * @param completedFlag the flag to be checked for completion of the word counting process
     * @param view the View component this task should publish its {@link Update}s onto
     */
    public UpdateConsumerTask(final ScheduledExecutorService executor,
                              final WordCounter wordCounter,
                              final CompletedFlag completedFlag,
                              final View view) {
        this.executor = Objects.requireNonNull(executor);
        this.wordCounter = Objects.requireNonNull(wordCounter);
        this.completedFlag = Objects.requireNonNull(completedFlag);
        this.view = Objects.requireNonNull(view);
    }

    @Override
    public void run() {
        final Optional<Update> optUpdate = this.wordCounter.dequeue();
        if (optUpdate.isPresent()) {
            final Update update = optUpdate.get();
            this.view.displayProgress(update.getFrequencies(), update.getProcessedWords());
        } else if (this.completedFlag.isCompleted()) {
            this.view.displayCompletion();
            this.executor.shutdown();
        }
    }
}
