package it.unibo.pcd.assignment2.executors.controller.executor;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class PausableExecutorImpl implements PausableExecutor{
    private static final int KEEP_ALIVE_TIME = 0;
    private static final TimeUnit UNIT = TimeUnit.MILLISECONDS;

    private final PausableThreadPoolExecutor executor;

    public PausableExecutorImpl(final int threadNumber) {
        this.executor = new PausableThreadPoolExecutor(threadNumber,threadNumber,
                KEEP_ALIVE_TIME, UNIT, new LinkedBlockingQueue<>());
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void execute(final Runnable command) {
        this.executor.execute(Objects.requireNonNull(command));
    }

    public void pause() {
        this.executor.pause();
    }

    public void resume() {
        this.executor.resume();
    }
}
