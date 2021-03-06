package it.unibo.pcd.assignment2.executors.model.entities.impl;

import it.unibo.pcd.assignment2.executors.model.entities.SourcePaths;

import java.nio.file.Path;
import java.util.Objects;

/**
 * An implementation of the {@link SourcePaths} interface.
 */
public class SourcePathsImpl implements SourcePaths {
    private final Path filesDirectory;
    private final Path stopwordsFile;

    /**
     * Default constructor.
     * @param filesDirectory the {@link Path} representing the folder in which the files to process are contained
     * @param stopwordsFile the {@link Path} representing the file which contains the stopwords
     */
    public SourcePathsImpl(final Path filesDirectory, final Path stopwordsFile) {
        this.filesDirectory = Objects.requireNonNull(filesDirectory);
        this.stopwordsFile = Objects.requireNonNull(stopwordsFile);
    }

    @Override
    public Path getFilesDirectoryPath() {
        return this.filesDirectory;
    }

    @Override
    public Path getStopwordsFilePath() {
        return this.stopwordsFile;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final SourcePathsImpl that = (SourcePathsImpl) o;
        return this.filesDirectory.equals(that.filesDirectory) && this.stopwordsFile.equals(that.stopwordsFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.filesDirectory, this.stopwordsFile);
    }

    @Override
    public String toString() {
        return "SourcePathsImpl{filesDirectory=" + this.filesDirectory + ", stopwordsFile=" + this.stopwordsFile + "}";
    }
}
