package it.unibo.pcd.assignment2.reactive.model.tasks;

import io.reactivex.rxjava3.functions.Function;
import it.unibo.pcd.assignment2.reactive.model.entities.Document;
import it.unibo.pcd.assignment2.reactive.model.entities.Page;
import it.unibo.pcd.assignment2.reactive.model.entities.impl.PageImpl;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * A task for transforming {@link Document}s into {@link Page}s.
 */
public class DocumentSplitterTask implements Function<Document, Stream<Page>> {
    @Override
    public Stream<Page> apply(final Document document) throws IOException {
        final PDFTextStripper stripper = new PDFTextStripper();
        final PDDocument pdfDocument = document.getInternalDocument();
        final List<Page> pages = new ArrayList<>();
        for (int page = 1; page <= pdfDocument.getNumberOfPages(); page++) {
            stripper.setStartPage(page);
            stripper.setEndPage(page);
            pages.add(new PageImpl(stripper.getText(pdfDocument)));
        }
        pdfDocument.close();
        return pages.stream();
    }
}
