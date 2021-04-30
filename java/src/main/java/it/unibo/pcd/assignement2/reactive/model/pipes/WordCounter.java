package it.unibo.pcd.assignement2.reactive.model.pipes;

import it.unibo.pcd.assignement2.reactive.model.entities.Update;

import java.util.Optional;

/**
 * An extension of a pipe for {@link Update}s which can also be drained of all resources contained in it.
 */
public interface WordCounter {
    Update processUpdate(Update update);
}
