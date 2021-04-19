package it.unibo.pcd.assignment2.executors.model.shared.impl;

import it.unibo.pcd.assignment2.executors.model.shared.SuspendedFlag;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An implementation of the {@link SuspendedFlag} interface.
 */
public class SuspendedFlagImpl implements SuspendedFlag {
    private final Lock lock;
    private final Condition suspendedCondition;
    private boolean isRunning;

    /**
     * Default constructor.
     */
    public SuspendedFlagImpl() {
        this.lock = new ReentrantLock();
        this.suspendedCondition = this.lock.newCondition();
        this.isRunning = true;
    }

    @Override
    public void check() {
        this.lock.lock();
        try {
            while (!this.isRunning) {
                try {
                    this.suspendedCondition.await();
                } catch (final InterruptedException ignored) {}
            }
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void setSuspended() {
        this.lock.lock();
        try {
            this.isRunning = false;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void setRunning() {
        this.lock.lock();
        try {
            this.isRunning = true;
            this.suspendedCondition.signalAll();
        } finally {
            this.lock.unlock();
        }
    }
}
