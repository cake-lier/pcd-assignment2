package it.unibo.pcd.assignment2.reactive.model.tasks;

import io.reactivex.rxjava3.functions.Function;
import it.unibo.pcd.assignment2.reactive.model.entities.Update;
import it.unibo.pcd.assignment2.reactive.model.entities.impl.UpdateImpl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A task for limiting the number of entries in an {@link Update} to display on the
 * {@link it.unibo.pcd.assignment2.reactive.view.View} component.
 */
public class UpdateProjectorTask implements Function<Update, Update> {
    private final int wordsNumber;

    /**
     * Default constructor.
     * @param wordsNumber the number of words to which limit the entries of the transformed {@link Update}
     */
    public UpdateProjectorTask(final int wordsNumber) {
        this.wordsNumber = wordsNumber;
    }

    @Override
    public Update apply(final Update update) {
        return new UpdateImpl(update.getFrequencies()
                                    .entrySet()
                                    .stream()
                                    .limit(this.wordsNumber)
                                    .collect(Collectors.toMap(Map.Entry::getKey,
                                                              Map.Entry::getValue,
                                                              Long::sum,
                                                              LinkedHashMap::new)),
                              update.getProcessedWords());
    }
}
