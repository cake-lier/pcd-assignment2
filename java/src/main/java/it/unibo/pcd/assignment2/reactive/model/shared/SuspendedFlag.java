package it.unibo.pcd.assignment2.reactive.model.shared;

/**
 * A settable flag that allows tasks to check whether their execution should continue or should be suspended.
 */
public interface SuspendedFlag {
    /**
     * It checks if the executions should continue or should be suspended. This method is blocking if the state of the flag is set
     * to "suspended".
     */
    void check();

    /**
     * It sets the state of this flag to "suspended".
     */
    void setSuspended();

    /**
     * It sets the state of this flag to "running".
     */
    void setRunning();
}
