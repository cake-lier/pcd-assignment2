package it.unibo.pcd.assignment2.executors.model.pipes;

import it.unibo.pcd.assignment2.executors.model.entities.Update;

import java.util.Optional;

/**
 * An extension of a pipe for {@link Update}s which can also be drained of all resources contained in it.
 */
public interface WordCounter {
    /**
     * It dequeues an element from this pipeline. This operation is blocking if no elements are present inside the pipeline,
     * unless the pipeline is closed. If it is closed, then an {@link Optional#empty()} returned.
     * @return the next element in the pipeline, if present, an {@link Optional#empty()} otherwise
     */
    Optional<Update> dequeue();

    /**
     * It enqueues an element into this pipeline. This operation is only possible if the pipeline is not closed, after that an
     * exception is thrown.
     * @param value the value to add to the pipeline
     */
    void enqueue(Update value);
}
