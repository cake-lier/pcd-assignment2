package it.unibo.pcd.assignment2.executors.model.tasks;

import it.unibo.pcd.assignment2.executors.model.entities.Document;
import it.unibo.pcd.assignment2.executors.model.entities.Page;
import it.unibo.pcd.assignment2.executors.model.entities.impl.PageImpl;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

/**
 * A task for transforming {@link Document}s into {@link Page}s.
 */
public class DocumentSplitterTask implements Function<Document, List<Page>> {
    @Override
    public List<Page> apply(final Document document) {
        try {
            final PDFTextStripper stripper = new PDFTextStripper();
            final PDDocument pdfDocument = document.getInternalDocument();
            final List<Page> pages = new ArrayList<>();
            for (int page = 1; page <= pdfDocument.getNumberOfPages(); page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                pages.add(new PageImpl(stripper.getText(pdfDocument)));
            }
            pdfDocument.close();
            return pages;
        } catch (final IOException ex) {
            throw new CompletionException(ex);
        }
    }
}
