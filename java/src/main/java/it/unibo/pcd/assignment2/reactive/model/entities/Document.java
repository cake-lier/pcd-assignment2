package it.unibo.pcd.assignment2.reactive.model.entities;

import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * A document entity as conceived into the problem space.
 */
public interface Document {
    /**
     * It returns the wrapped document object as defined by the Apache PDFBox library.
     * @return the wrapped document object as defined by the Apache PDFBox library
     */
    PDDocument getInternalDocument();
}
