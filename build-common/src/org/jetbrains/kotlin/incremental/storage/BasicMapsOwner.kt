/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental.storage

import org.jetbrains.annotations.TestOnly
import java.io.File

open class BasicMapsOwner(val cachesDir: File) {
    private val maps = arrayListOf<BasicMap<*, *>>()

    companion object {
        val CACHE_EXTENSION = "tab"
    }

    protected val String.storageFile: File
        get() = File(cachesDir, this + "." + CACHE_EXTENSION)

    protected fun <K, V, M : BasicMap<K, V>> registerMap(map: M): M {
        maps.add(map)
        return map
    }

    open fun clean() {
        maps.forEach { it.clean() }
    }

    open fun close() {
        maps.forEach { it.close() }
    }

    open fun flush(memoryCachesOnly: Boolean) {
        maps.forEach { it.flush(memoryCachesOnly) }
    }

    @TestOnly fun dump(): String = maps.joinToString("\n\n") { it.dump() }
}