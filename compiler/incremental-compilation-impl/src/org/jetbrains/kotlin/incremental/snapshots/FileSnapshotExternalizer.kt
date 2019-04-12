/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental.snapshots

import com.intellij.util.io.DataExternalizer
import java.io.DataInput
import java.io.DataOutput
import java.io.File

object FileSnapshotExternalizer : DataExternalizer<FileSnapshot> {
    override fun save(out: DataOutput, value: FileSnapshot) {
        out.writeUTF(value.file.canonicalPath)
        out.writeLong(value.length)
        out.writeInt(value.hash.size)
        out.write(value.hash)
    }

    override fun read(input: DataInput): FileSnapshot {
        val file = File(input.readUTF())
        val length = input.readLong()
        val hashSize = input.readInt()
        val hash = ByteArray(hashSize)
        input.readFully(hash)
        return FileSnapshot(file, length, hash)
    }
}