package it.unibo.pcd.assignment2.executors.model.shared;

/**
 * A flag which represents whether or not the word counting process has completed. This means that the only task left
 * executing in the system is the {@link it.unibo.pcd.assignment2.executors.controller.tasks.UpdateConsumerTask} and after
 * which the operation is completed and the system can be shutdown.
 */
public interface CompletedFlag {
    /**
     * It sets the state of the word processing operation as completed, so as to notify the last task to not wait for new
     * incoming updates anymore.
     */
    void setComplete();

    /**
     * Returns whether or not the word processing operation is completed.
     * @return whether or not the word processing operation is completed
     */
    boolean isCompleted();
}
