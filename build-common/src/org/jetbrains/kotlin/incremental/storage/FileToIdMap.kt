/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental.storage

import java.io.File

internal class FileToIdMap(file: File) : BasicMap<File, Int>(file, FileKeyDescriptor, IntExternalizer) {
    override fun dumpKey(key: File): String = key.toString()

    override fun dumpValue(value: Int): String = value.toString()

    operator fun get(file: File): Int? = storage[file]

    operator fun set(file: File, id: Int) {
        storage[file] = id
    }

    fun remove(file: File) {
        storage.remove(file)
    }

    fun toMap(): Map<File, Int> {
        val result = HashMap<File, Int>()
        for (key in storage.keys) {
            val value = storage[key] ?: continue
            result[key] = value
        }
        return result
    }
}
