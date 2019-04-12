/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental.snapshots

import java.io.File
import java.util.*

class FileSnapshot(
        val file: File,
        val length: Long,
        val hash: ByteArray
) {
    init {
        assert(!file.isDirectory)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other::class.java != this::class.java) return false

        other as FileSnapshot

        if (file != other.file) return false
        if (length != other.length) return false
        if (!Arrays.equals(hash, other.hash)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = file.hashCode()
        result = 31 * result + length.hashCode()
        result = 31 * result + Arrays.hashCode(hash)
        return result
    }

    override fun toString(): String {
        return "FileSnapshot(file=$file, length=$length, hash=${Arrays.toString(hash)})"
    }
}
