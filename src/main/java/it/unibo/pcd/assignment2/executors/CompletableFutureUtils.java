package it.unibo.pcd.assignment2.executors;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CompletableFutureUtils {
    private CompletableFutureUtils() {}

    public static <T> CompletableFuture<Collection<T>> allOf(final Collection<? extends CompletableFuture<T>> futures) {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                                .thenApply(unused -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }
}
