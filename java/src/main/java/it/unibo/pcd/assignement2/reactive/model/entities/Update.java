package it.unibo.pcd.assignement2.reactive.model.entities;

import java.util.Map;

/**
 * An update entity which contains all necessary information to be displayed to the user.
 */
public interface Update {
    /**
     * It returns the most frequent words associated with their frequencies at a specific point in time.
     * @return the most frequent words associated with their frequencies
     */
    Map<String, Long> getFrequencies();

    /**
     * It returns the processed words in total at a specific point in time.
     * @return the processed words in total
     */
    long getProcessedWords();
}
