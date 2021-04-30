package it.unibo.pcd.assignement2.reactive.controller.executor;

import io.reactivex.rxjava3.core.Scheduler;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class PausableExecutorImpl implements PausableExecutor {
    private final PausableThreadPoolExecutor executor;

    public PausableExecutorImpl(final int corePoolSize,final long keepAliveTime,final TimeUnit unit) {
        this.executor = new PausableThreadPoolExecutor(corePoolSize,Integer.MAX_VALUE,
                keepAliveTime, unit, new LinkedBlockingQueue<>());
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
