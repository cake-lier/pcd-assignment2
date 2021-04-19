package it.unibo.pcd.assignment2.executors.model.tasks;

import it.unibo.pcd.assignment2.executors.model.entities.Update;
import it.unibo.pcd.assignment2.executors.model.pipes.WordCounter;

import java.util.Objects;
import java.util.function.Consumer;

public class WordEnqueueingTask implements Consumer<Update> {
    private final WordCounter wordCounter;

    public WordEnqueueingTask(final WordCounter wordCounter) {
        this.wordCounter = Objects.requireNonNull(wordCounter);
    }

    @Override
    public void accept(final Update update) {
        this.wordCounter.enqueue(update);
    }
}
