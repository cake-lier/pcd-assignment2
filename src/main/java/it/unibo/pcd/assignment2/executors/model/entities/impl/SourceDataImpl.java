package it.unibo.pcd.assignment2.executors.model.entities.impl;

import it.unibo.pcd.assignment2.executors.model.entities.SourceData;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SourceDataImpl implements SourceData {
    private final List<Path> pdfFiles;
    private final Set<String> stopwords;

    public SourceDataImpl(final List<Path> pdfFiles, final Set<String> stopwords) {
        this.pdfFiles = new ArrayList<>(pdfFiles);
        this.stopwords = new HashSet<>(stopwords);
    }

    @Override
    public List<Path> getPDFFiles() {
        return Collections.unmodifiableList(this.pdfFiles);
    }

    @Override
    public Set<String> getStopwords() {
        return Collections.unmodifiableSet(this.stopwords);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final SourceDataImpl that = (SourceDataImpl) o;
        return this.pdfFiles.equals(that.pdfFiles) && this.stopwords.equals(that.stopwords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pdfFiles, this.stopwords);
    }

    @Override
    public String toString() {
        return "SourceDataImpl{pdfFiles=" + this.pdfFiles + ", stopwords=" + this.stopwords + "}";
    }
}
