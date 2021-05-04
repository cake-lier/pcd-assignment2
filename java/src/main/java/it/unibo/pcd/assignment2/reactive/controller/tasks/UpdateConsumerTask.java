package it.unibo.pcd.assignment2.reactive.controller.tasks;

import io.reactivex.rxjava3.functions.Consumer;
import it.unibo.pcd.assignment2.reactive.model.entities.Update;
import it.unibo.pcd.assignment2.reactive.view.View;

import java.util.Objects;

/**
 * A task for collecting the results of the computation, the {@link Update}s, and publish them on the {@link View} component.
 */
public class UpdateConsumerTask implements Consumer<Update> {
    private final View view;

    /**
     * Default constructor.
     * @param view the View component this task should publish its {@link Update}s onto
     */
    public UpdateConsumerTask(final View view) {
        this.view = Objects.requireNonNull(view);
    }

    @Override
    public void accept(final Update update) {
        this.view.displayProgress(update.getFrequencies(), update.getProcessedWords());
    }
}
