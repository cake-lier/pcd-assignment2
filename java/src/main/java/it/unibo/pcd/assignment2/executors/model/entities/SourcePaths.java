package it.unibo.pcd.assignment2.executors.model.entities;

import java.nio.file.Path;

/**
 * The {@link Path}s used as source for the data which is used as input of the word counting process. These {@link Path}s are
 * the one representing the folder in which the files to process are contained and the one representing the file which contains
 * the stopwords.
 */
public interface SourcePaths {
    /**
     * It returns the {@link Path} representing the folder in which the files to process are contained
     * @return the {@link Path} representing the folder in which the files to process are contained
     */
    Path getFilesDirectoryPath();

    /**
     * It returns the {@link Path} representing the file which contains the stopwords
     * @return the {@link Path} representing the file which contains the stopwords
     */
    Path getStopwordsFilePath();
}
