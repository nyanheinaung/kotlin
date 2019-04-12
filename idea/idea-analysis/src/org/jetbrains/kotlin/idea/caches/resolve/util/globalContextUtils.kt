/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.caches.resolve.util

import org.jetbrains.kotlin.context.GlobalContextImpl
import org.jetbrains.kotlin.storage.ExceptionTracker
import org.jetbrains.kotlin.storage.LockBasedStorageManager

internal fun GlobalContextImpl.contextWithNewLockAndCompositeExceptionTracker(): GlobalContextImpl {
    val newExceptionTracker = CompositeExceptionTracker(this.exceptionTracker)
    return GlobalContextImpl(
        LockBasedStorageManager.createWithExceptionHandling("GlobalContextUtils", newExceptionTracker),
        newExceptionTracker
    )
}

private class CompositeExceptionTracker(val delegate: ExceptionTracker) : ExceptionTracker() {
    override fun getModificationCount(): Long {
        return super.getModificationCount() + delegate.modificationCount
    }
}
