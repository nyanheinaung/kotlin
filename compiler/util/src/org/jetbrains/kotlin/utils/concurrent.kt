/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.utils.concurrent.block

class LockedClearableLazyValue<out T: Any>(val lock: Any, val init: () -> T) {
    @Volatile private var value: T? = null

    fun get(): T {
        val _v1 = value
        if (_v1 != null) {
            return _v1
        }

        return synchronized(lock) {
            val _v2 = value
            // Suppress because of https://youtrack.jetbrains.com/issue/KT-6176
            @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
            if (_v2 != null) {
                _v2!!
            }
            else {
                val _v3 = init()
                this.value = _v3
                _v3
            }
        }
    }

    fun drop() {
        synchronized (lock) {
            value = null
        }
    }
}

