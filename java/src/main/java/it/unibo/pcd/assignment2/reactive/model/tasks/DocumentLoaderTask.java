package it.unibo.pcd.assignment2.reactive.model.tasks;

import io.reactivex.rxjava3.functions.Function;
import it.unibo.pcd.assignment2.reactive.model.entities.Document;
import it.unibo.pcd.assignment2.reactive.model.entities.impl.DocumentImpl;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.nio.file.Path;

/**
 * A task for transforming {@link Path}s into {@link Document}s.
 */
public class DocumentLoaderTask implements Function<Path, Document> {
    @Override
    public Document apply(final Path path) throws IOException {
        return new DocumentImpl(PDDocument.load(path.toFile()));
    }
}
