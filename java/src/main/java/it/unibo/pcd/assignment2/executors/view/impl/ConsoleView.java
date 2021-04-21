package it.unibo.pcd.assignment2.executors.view.impl;

import it.unibo.pcd.assignment2.executors.controller.Controller;
import it.unibo.pcd.assignment2.executors.controller.impl.ControllerImpl;
import it.unibo.pcd.assignment2.executors.view.View;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An implementation of the {@link View} interface which instantiates a Command Line Interface.
 */
public class ConsoleView implements View {
    private static final String NOT_ENOUGH_ARGUMENTS_ERROR = "Not enough arguments";
    private static final String APP_TITLE = "\n--- Statistics for given files ---\n";
    private static final String FREQUENCIES_TITLE = "The %d most frequent words:\n";
    private static final String FREQUENCIES_LINE = "- \"%s\": %d\n";
    private static final String PROCESSED_WORDS_TITLE = "\nTotal processed words:\n- %d\n";
    private static final String ERROR_MSG_PREFIX = "An error has occurred: %s\n";
    private static final String END_MESSAGE = "\n--- End ---";

    private final Controller controller;
    private final Path filesDirectory;
    private final int numberWords;
    private final Path stopwordsFile;
    private long lastProcessedWords;
    private Map<String, Long> lastFrequencies;

    /**
     * Default constructor.
     * @param arguments the list of arguments passed to the application at launch
     */
    public ConsoleView(final List<String> arguments) {
        this.controller = new ControllerImpl(this);
        if (arguments.size() < 3) {
            System.err.println(NOT_ENOUGH_ARGUMENTS_ERROR);
            this.controller.exit();
        }
        this.filesDirectory = Paths.get(arguments.get(0));
        this.numberWords = Integer.parseInt(arguments.get(1));
        this.stopwordsFile = Paths.get(arguments.get(2));
        this.lastProcessedWords = 0;
        this.lastFrequencies = Collections.emptyMap();
        this.show();
    }

    @Override
    public void displayProgress(final Map<String, Long> frequencies, final long processedWords) {
        this.lastFrequencies = new LinkedHashMap<>(frequencies);
        this.lastProcessedWords = processedWords;
    }

    @Override
    public void displayCompletion() {
        System.out.println(APP_TITLE);
        System.out.printf(FREQUENCIES_TITLE, this.lastFrequencies.size());
        this.lastFrequencies.forEach((w, f) -> System.out.printf(FREQUENCIES_LINE, w, f));
        System.out.printf(PROCESSED_WORDS_TITLE, this.lastProcessedWords);
        System.out.println(END_MESSAGE);
    }

    @Override
    public void displayError(final String message) {
        System.err.printf(ERROR_MSG_PREFIX, message);
    }

    /*
     * It launches the computation.
     */
    private void show() {
        this.controller.launch(this.filesDirectory, this.stopwordsFile, this.numberWords);
    }
}
