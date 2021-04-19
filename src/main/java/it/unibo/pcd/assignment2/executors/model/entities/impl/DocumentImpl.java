package it.unibo.pcd.assignment2.executors.model.entities.impl;

import it.unibo.pcd.assignment2.executors.model.entities.Document;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.util.Objects;

/**
 * An implementation of the {@link Document} interface.
 */
public class DocumentImpl implements Document {
    private final PDDocument internalDocument;

    /**
     * Default constructor.
     * @param internalDocument the wrapped document object as defined by the Apache PDFBox library
     */
    public DocumentImpl(final PDDocument internalDocument) {
        this.internalDocument = Objects.requireNonNull(internalDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PDDocument getInternalDocument() {
        return this.internalDocument;
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
        return this.internalDocument.equals(((DocumentImpl) o).internalDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.internalDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "DocumentImpl{internalDocument=" + this.internalDocument + '}';
    }
}
