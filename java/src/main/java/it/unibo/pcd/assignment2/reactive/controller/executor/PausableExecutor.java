package it.unibo.pcd.assignment2.reactive.controller.executor;

import java.util.concurrent.Executor;

public interface PausableExecutor extends Executor {
    void pause();

    void resume();
}

