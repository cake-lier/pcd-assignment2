package it.unibo.pcd.assignment2.reactive.model.entities.impl;

import it.unibo.pcd.assignment2.reactive.model.entities.Update;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * An implementation of the {@link Update} interface.
 */
public class UpdateImpl implements Update {
    private final Map<String, Long> frequencies;
    private final long processedWords;

    /**
     * Default constructor.
     * @param frequencies the most frequent words associated with their frequencies at a specific point in time
     * @param processedWords the processed words in total at a specific point in time
     */
    public UpdateImpl(final Map<String, Long> frequencies, final long processedWords) {
        this.frequencies = Collections.unmodifiableMap(frequencies);
        this.processedWords = processedWords;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Long> getFrequencies() {
        return this.frequencies;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getProcessedWords() {
        return this.processedWords;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final UpdateImpl update = (UpdateImpl) o;
        return this.processedWords == update.processedWords && this.frequencies.equals(update.frequencies);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.frequencies, this.processedWords);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "UpdateImpl{frequencies=" + this.frequencies + ", processedWords=" + this.processedWords + '}';
    }
}
