package it.unibo.pcd.assignment2.reactive.controller.tasks;

import io.reactivex.rxjava3.functions.Action;
import it.unibo.pcd.assignment2.reactive.view.View;

import java.util.Objects;

public class ComputationEndTask implements Action {
    private final View view;

    public ComputationEndTask(final View view) {
        this.view = Objects.requireNonNull(view);
    }


    @Override
    public void run() {
        view.displayCompletion();
    }
}
