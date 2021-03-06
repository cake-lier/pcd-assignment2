package it.unibo.pcd.assignment2.executors.model.tasks;

import it.unibo.pcd.assignment2.executors.model.entities.Update;
import it.unibo.pcd.assignment2.executors.model.entities.impl.UpdateImpl;
import it.unibo.pcd.assignment2.executors.model.entities.Page;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The task for counting the words in a {@link Page} and collecting them in an {@link Update}.
 */
public class WordCounterTask implements Function<Page, Update> {
    private final Set<String> stopwords;

    /**
     * Default constructor.
     * @param stopwords the set of words to be ignored during the counting process but which are counted as processed words anyway
     */
    public WordCounterTask(final Set<String> stopwords) {
        this.stopwords = Objects.requireNonNull(stopwords);
    }

    @Override
    public Update apply(final Page page) {
        final String[] words = Pattern.compile("\\W+").split(page.getText());
        return new UpdateImpl(Arrays.stream(words)
                                    .map(String::toLowerCase)
                                    .filter(w -> !this.stopwords.contains(w))
                                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())),
                              words.length);
    }
}
