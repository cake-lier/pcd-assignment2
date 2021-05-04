package it.unibo.pcd.assignment2.reactive.model.tasks;

import io.reactivex.rxjava3.functions.BiFunction;
import it.unibo.pcd.assignment2.reactive.model.entities.Update;
import it.unibo.pcd.assignment2.reactive.model.entities.impl.UpdateImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The task to be used for aggregating a new {@link Update} to the one used as aggregator for all previous {@link Update}s.
 */
public class WordAggregatorTask implements BiFunction<Update, Update, Update> {
    @Override
    public Update apply(final Update accumulated, final Update current) {
        final Map<String, Long> totalFrequencies = new HashMap<>(accumulated.getFrequencies());
        final long totalProcessedWords = accumulated.getProcessedWords() + current.getProcessedWords();
        current.getFrequencies().forEach((k, v) -> totalFrequencies.merge(k, v, Long::sum));
        return new UpdateImpl(
                totalFrequencies.entrySet()
                                .parallelStream()
                                .sorted(Collections.<Map.Entry<String, Long>>reverseOrder(Map.Entry.comparingByValue())
                                                   .thenComparing(Map.Entry.comparingByKey()))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::sum, LinkedHashMap::new)),
                totalProcessedWords
        );
    }
}
