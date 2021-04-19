package it.unibo.pcd.assignment2.executors.view;

import java.util.Map;

/**
 * The View component of this application. It should capture user input and be notified of changes into the Model component which
 * should appear to the user.
 */
public interface View {
    /**
     * It displays the progress of the current computation by receiving updated information about it. This information is made
     * of the most frequent words associated with their frequencies and the total number of processed words until this very
     * moment.
     * @param frequencies a map containing the most frequent words associated with their frequencies
     * @param processedWords the total number of currently processed words
     */
    void displayProgress(Map<String, Long> frequencies, long processedWords);

    /**
     * It displays the completion of the computation, that the computation has ended.
     */
    void displayCompletion();

    /**
     * It displays an error message given the text of the message itself.
     * @param message the text of the error message to display
     */
    void displayError(String message);
}
