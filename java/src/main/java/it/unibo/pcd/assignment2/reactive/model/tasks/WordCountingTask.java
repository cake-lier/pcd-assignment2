package it.unibo.pcd.assignment2.reactive.model.tasks;

import io.reactivex.rxjava3.functions.Function;
import it.unibo.pcd.assignment2.reactive.model.entities.Page;
import it.unibo.pcd.assignment2.reactive.model.entities.Update;
import it.unibo.pcd.assignment2.reactive.model.entities.impl.UpdateImpl;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WordCountingTask implements Function<Page, Update> {
    private final Set<String> stopwords;

    public WordCountingTask(final Set<String> stopwords) {
        this.stopwords = Objects.requireNonNull(stopwords);
    }

    @Override
    public Update apply(final Page page) {
        final String[] words = Pattern.compile("\\W+").split(page.getText());
        return new UpdateImpl(Arrays.stream(words)
                                    .map(String::toLowerCase)
                                    .filter(w -> !this.stopwords.contains(w))
                                    .collect(Collectors.groupingBy(java.util.function.Function.identity(), Collectors.counting())),
                              words.length);
    }
}
