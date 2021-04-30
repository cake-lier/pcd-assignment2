package it.unibo.pcd.assignement2.reactive.model.entities;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public interface SourceData {
    List<Path> getPDFFiles();

    Set<String> getStopwords();
}