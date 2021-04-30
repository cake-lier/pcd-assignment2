package it.unibo.pcd.assignement2.reactive.model.pipes.impl;

import it.unibo.pcd.assignement2.reactive.model.entities.Update;
import it.unibo.pcd.assignement2.reactive.model.entities.impl.UpdateImpl;
import it.unibo.pcd.assignement2.reactive.model.pipes.WordCounter;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * An implementation of the {@link WordCounter} interface.
 */
public class WordCounterImpl implements WordCounter{
    private final int wordsNumber;
    private final Map<String, Long> frequencies;
    private int processedWords;

    /**
     * Default constructor.
     * @param wordsNumber the maximum number of most frequent words an {@link Update} should show
     */
    public WordCounterImpl(final int wordsNumber) {
        this.wordsNumber = wordsNumber;
        this.frequencies = new HashMap<>();
        this.processedWords = 0;
    }

    @Override
    public synchronized final Update processUpdate(final Update update){
        update.getFrequencies().forEach((k, v) -> this.frequencies.merge(k, v, Long::sum));
        this.processedWords += update.getProcessedWords();
        return new UpdateImpl(
                this.frequencies.entrySet()
                        .parallelStream()
                        .sorted(Collections.<Map.Entry<String, Long>>reverseOrder(Map.Entry.comparingByValue())
                                .thenComparing(Map.Entry.comparingByKey()))
                        .limit(this.wordsNumber)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::sum, LinkedHashMap::new)),
                this.processedWords
        );

    }
}
