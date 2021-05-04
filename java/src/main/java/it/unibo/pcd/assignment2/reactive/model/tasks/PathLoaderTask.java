package it.unibo.pcd.assignment2.reactive.model.tasks;

import io.reactivex.rxjava3.functions.Function;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * A task for generating the inputs of the computation, the {@link Path}s of the files to process.
 */
public class PathLoaderTask implements Function<Path, Stream<Path>> {
    @Override
    public Stream<Path> apply(final Path filesDirectory) throws IOException {
        return Files.list(filesDirectory).filter(p -> p.toString().matches(".*pdf$"));
    }
}
