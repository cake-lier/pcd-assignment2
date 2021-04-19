package it.unibo.pcd.assignment2.executors.controller.executor;

import it.unibo.pcd.assignment2.executors.model.shared.SuspendedFlag;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SuspendableThreadPoolExecutor extends ThreadPoolExecutor {
    private static final int KEEP_ALIVE_TIME = 0;

    final SuspendedFlag suspendedFlag;

    public SuspendableThreadPoolExecutor(final int threadNumber, final SuspendedFlag suspendedFlag) {
        super(threadNumber, threadNumber, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        this.suspendedFlag = Objects.requireNonNull(suspendedFlag);
    }

    protected void beforeExecute(final Thread thread, final Runnable runnable) {
        this.suspendedFlag.check();
    }
}
