package it.unibo.pcd.assignement2.reactive.controller.executor;

import it.unibo.pcd.assignement2.reactive.model.shared.SuspendedFlag;

import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

public class SuspendableForkJoinPool extends ForkJoinPool {
    private final SuspendedFlag suspendedFlag;

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