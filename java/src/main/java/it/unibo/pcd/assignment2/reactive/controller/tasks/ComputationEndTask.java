package it.unibo.pcd.assignment2.reactive.controller.tasks;

import io.reactivex.rxjava3.functions.Action;
import it.unibo.pcd.assignment2.reactive.view.View;

import java.util.Objects;

/**
 * A task to be executed at the end of the word counting process.
 */
public class ComputationEndTask implements Action {
    private final View view;

    /**
     * Default constructor.
     * @param view the {@link View} component to be notified of the end of the word counting process
     */
    public ComputationEndTask(final View view) {
        this.view = Objects.requireNonNull(view);
    }

    @Override
    public void run() {
        this.view.displayCompletion();
    }
}
