/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental.storage

import com.intellij.util.io.ExternalIntegerKeyDescriptor
import java.io.File

internal class IdToFileMap(file: File) : BasicMap<Int, File>(file, ExternalIntegerKeyDescriptor(), FileKeyDescriptor) {
    override fun dumpKey(key: Int): String = key.toString()

    override fun dumpValue(value: File): String = value.toString()

    operator fun get(id: Int): File? = storage[id]

    operator fun contains(id: Int): Boolean = id in storage

    operator fun set(id: Int, file: File) {
        storage[id] = file
    }

    fun remove(id: Int) {
        storage.remove(id)
    }
}
