package it.unibo.pcd.assignement2.reactive.model.tasks;

import io.reactivex.rxjava3.functions.Function;
import it.unibo.pcd.assignement2.reactive.model.entities.SourceData;
import it.unibo.pcd.assignement2.reactive.model.entities.SourcePaths;
import it.unibo.pcd.assignement2.reactive.model.entities.impl.SourceDataImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

/**
 * A task for generating the inputs of the computation, the {@link Path}s.
 */
public class PathLoaderTask implements Function<SourcePaths, SourceData> {
    @Override
    public SourceData apply(final SourcePaths sourcePaths) {
        try {
            return new SourceDataImpl(Files.list(sourcePaths.getFilesDirectoryPath())
                                           .filter(p -> p.toString().matches(".*pdf$"))
                                           .collect(Collectors.toList()),
                                      new HashSet<>(Files.readAllLines(sourcePaths.getStopwordsFilePath())));
        } catch (final IOException ex) {
            throw new CompletionException(ex);
        }
    }
}
