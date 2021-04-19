package it.unibo.pcd.assignment2.executors.main;

import it.unibo.pcd.assignment2.executors.view.impl.ConsoleView;

import java.util.Arrays;

/**
 * The main class for the application with a command line interface for the user.
 */
public class ConsoleMain {
    private ConsoleMain() {}

    /**
     * The main method.
     * @param args the arguments the application receives as input. These should be, in order, the path to the directory
     *             containing the PDF to process, the number of most frequent words to display and the path to the file
     *             containing the stopwords. A optional fourth boolean parameter can be passed and, if set to <code>true</code>,
     *             makes the application print the time taken to execute.
     */
    public static void main(final String[] args) {
        new ConsoleView(Arrays.asList(args));
    }
}
