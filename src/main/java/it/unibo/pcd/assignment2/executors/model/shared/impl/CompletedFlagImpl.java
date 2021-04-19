package it.unibo.pcd.assignment2.executors.model.shared.impl;

import it.unibo.pcd.assignment2.executors.model.shared.CompletedFlag;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CompletedFlagImpl implements CompletedFlag {
    private final Lock lock;
    private boolean isClosed;

    public CompletedFlagImpl() {
        this.lock = new ReentrantLock();
        this.isClosed = false;
    }

    @Override
    public final void setComplete() {
        this.lock.lock();
        try {
            this.isClosed = true;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean isCompleted() {
        this.lock.lock();
        try {
            return this.isClosed;
        } finally {
            this.lock.unlock();
        }
    }
}
