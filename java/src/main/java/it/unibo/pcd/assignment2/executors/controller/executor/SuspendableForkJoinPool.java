package it.unibo.pcd.assignment2.executors.controller.executor;

import it.unibo.pcd.assignment2.executors.model.shared.SuspendedFlag;

import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

/**
 * A {@link ForkJoinPool} which can also be suspended. It can be done by passing a {@link SuspendedFlag} which will be checked
 * before executing any new task. If the flag is set to "suspend", the execution will be suspended until the flag is reset.
 */
public class SuspendableForkJoinPool extends ForkJoinPool {
    private final SuspendedFlag suspendedFlag;

    /**
     * Default constructor.
     * @param threadNumber the number of threads to dedicate to this {@link ForkJoinPool}
     * @param suspendedFlag the flag to be checked for suspension
     */
    public SuspendableForkJoinPool(final int threadNumber, final SuspendedFlag suspendedFlag) {
        super(threadNumber);
        this.suspendedFlag = Objects.requireNonNull(suspendedFlag);
    }

    @Override
    public void execute(final Runnable runnable) {
        this.suspendedFlag.check();
        super.execute(runnable);
    }
}
