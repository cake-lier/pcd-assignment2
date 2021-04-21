package it.unibo.pcd.assignment2.executors.model.pipes.impl;

import it.unibo.pcd.assignment2.executors.model.entities.Update;
import it.unibo.pcd.assignment2.executors.model.entities.impl.UpdateImpl;
import it.unibo.pcd.assignment2.executors.model.pipes.WordCounter;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * An implementation of the {@link WordCounter} interface.
 */
public class WordCounterImpl implements WordCounter {
    private final Queue<Update> queue;
    private final Lock lock;
    private final int wordsNumber;
    private final Map<String, Long> frequencies;
    private int processedWords;

    /**
     * Default constructor.
     * @param wordsNumber the maximum number of most frequent words an {@link Update} should show
     */
    public WordCounterImpl(final int wordsNumber) {
        this.queue = new LinkedList<>();
        this.lock = new ReentrantLock();
        this.wordsNumber = wordsNumber;
        this.frequencies = new HashMap<>();
        this.processedWords = 0;
    }

    @Override
    public final Optional<Update> dequeue() {
        this.lock.lock();
        try {
            Optional<Update> lastUpdate;
            do {
                lastUpdate = Optional.ofNullable(this.queue.poll());
            } while (!this.queue.isEmpty());
            return lastUpdate;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public final void enqueue(final Update value) {
        this.lock.lock();
        try {
            value.getFrequencies().forEach((k, v) -> this.frequencies.merge(k, v, Long::sum));
            this.processedWords += value.getProcessedWords();
            this.queue.add(new UpdateImpl(
                this.frequencies.entrySet()
                                .parallelStream()
                                .sorted(Collections.<Map.Entry<String, Long>>reverseOrder(Map.Entry.comparingByValue())
                                                   .thenComparing(Map.Entry.comparingByKey()))
                                .limit(this.wordsNumber)
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::sum, LinkedHashMap::new)),
                this.processedWords
            ));
        } finally {
            this.lock.unlock();
        }
    }
}
