package it.unibo.pcd.assignment2.reactive.model.tasks;

import io.reactivex.rxjava3.functions.Function;
import it.unibo.pcd.assignment2.reactive.model.entities.Update;
import it.unibo.pcd.assignment2.reactive.model.pipes.WordCounter;

import java.util.Objects;

public class WordEnqueueingTask implements Function<Update,Update> {
    private final WordCounter wordCounter;

    public WordEnqueueingTask(final WordCounter wordCounter) {
        this.wordCounter = Objects.requireNonNull(wordCounter);
    }

    @Override
    public Update apply(Update update) {
        return this.wordCounter.processUpdate(update);
    }
}
