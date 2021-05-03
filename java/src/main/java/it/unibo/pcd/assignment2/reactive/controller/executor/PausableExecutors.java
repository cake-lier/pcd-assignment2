package it.unibo.pcd.assignment2.reactive.controller.executor;

import java.util.concurrent.TimeUnit;

public class PausableExecutors {
    public static PausableExecutor cachedThreadPool(int corePoolSize){
        return new PausableExecutorImpl(0,60L, TimeUnit.SECONDS);
    }
    public static PausableExecutor cachedThreadPool(){
        return cachedThreadPool(0);
    }
    public static PausableExecutor fixedThreadPool(int corePoolSize){
        return new PausableExecutorImpl(corePoolSize,0, TimeUnit.MICROSECONDS);

    }
}
