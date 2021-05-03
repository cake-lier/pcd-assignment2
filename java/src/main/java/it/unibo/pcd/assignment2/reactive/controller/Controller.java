package it.unibo.pcd.assignment2.reactive.controller;

import java.nio.file.Path;

/**
 * The Controller component of this application, it should represent the application itself. That being so, it receives user
 * input from the View component and notifies it of changes in the Model component state. It should also be capable of notifying
 * the Model of requests made by the user and receive the adequate response. At last, it should manage the application state.
 */
public interface Controller {
    /**
     * It launches a new computation with the inputs given.
     * @param filesDirectory the path of the directory containing the PDF files to process
     * @param stopwordsFile the path of the file containing the stopwords
     * @param wordsNumber the number of most frequent words to display
     */
    void launch(Path filesDirectory, Path stopwordsFile, final int wordsNumber);

    /**
     * It notifies the Model to suspend the currently running computation.
     */
    void suspend();

    /**
     * It notifies the Model to resume the currently suspended computation.
     */
    void resume();

    /**
     * It exits the application.
     */
    void exit();
}
