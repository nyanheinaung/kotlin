/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.storage;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/*package*/ class NoLock implements Lock {
    public static final Lock INSTANCE = new NoLock();

    private NoLock() {
    }

    @Override
    public void lock() {
        // Do nothing
    }

    @Override
    public void unlock() {
        // Do nothing
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new UnsupportedOperationException("Should not be called");
    }

    @Override
    public boolean tryLock() {
        throw new UnsupportedOperationException("Should not be called");
    }

    @Override
    public boolean tryLock(long time, @NotNull TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("Should not be called");
    }

    @NotNull
    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("Should not be called");
    }
}
