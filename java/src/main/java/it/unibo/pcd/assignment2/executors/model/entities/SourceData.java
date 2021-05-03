package it.unibo.pcd.assignment2.executors.model.entities;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * The data used as input for the word counting process. It is made of the collection of all {@link Path}s of the files to
 * process and the set of words to used as stopwords.
 */
public interface SourceData {
    /**
     * It returns all {@link Path}s of the files to process.
     * @return all {@link Path}s of the files to process
     */
    List<Path> getPDFFiles();

    /**
     * It returns the set of words to used as stopwords.
     * @return the set of words to used as stopwords
     */
    Set<String> getStopwords();
}
