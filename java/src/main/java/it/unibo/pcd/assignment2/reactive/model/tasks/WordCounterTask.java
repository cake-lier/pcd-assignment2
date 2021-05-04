package it.unibo.pcd.assignment2.reactive.model.tasks;

import io.reactivex.rxjava3.functions.BiFunction;
import it.unibo.pcd.assignment2.reactive.model.entities.Page;
import it.unibo.pcd.assignment2.reactive.model.entities.Update;
import it.unibo.pcd.assignment2.reactive.model.entities.impl.UpdateImpl;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The task for counting the words in a {@link Page} and collecting them in an {@link Update}.
 */
public class WordCounterTask implements BiFunction<Page, Set<String>, Update> {
    @Override
    public Update apply(final Page page, final Set<String> stopwords) {
        final String[] words = Pattern.compile("\\W+").split(page.getText());
        return new UpdateImpl(Arrays.stream(words)
                                    .map(String::toLowerCase)
                                    .filter(w -> !stopwords.contains(w))
                                    .collect(Collectors.groupingBy(x -> x, Collectors.counting())),
                              words.length);
    }
}
