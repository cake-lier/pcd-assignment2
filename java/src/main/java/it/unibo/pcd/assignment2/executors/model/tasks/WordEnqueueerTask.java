package it.unibo.pcd.assignment2.executors.model.tasks;

import it.unibo.pcd.assignment2.executors.model.entities.Update;
import it.unibo.pcd.assignment2.executors.model.pipes.WordCounter;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * The task to be used for enqueueing new {@link Update}s into the {@link WordCounter}.
 */
public class WordEnqueueerTask implements Consumer<Update> {
    private final WordCounter wordCounter;

    /**
     * Default constructor.
     * @param wordCounter the {@link WordCounter} to be filled by the incoming {@link Update}s
     */
    public WordEnqueueerTask(final WordCounter wordCounter) {
        this.wordCounter = Objects.requireNonNull(wordCounter);
    }

    @Override
    public void accept(final Update update) {
        this.wordCounter.enqueue(update);
    }
}
