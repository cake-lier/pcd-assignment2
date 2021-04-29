package it.unibo.pcd.assignement2.reactive.model.shared;

public interface CompletedFlag {
    /**
     * It closes this pipeline, waking up all tasks waiting for dequeueing an element.
     */
    void setComplete();

    boolean isCompleted();
}
