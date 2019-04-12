/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental.snapshots

import org.jetbrains.kotlin.incremental.ChangedFiles
import org.jetbrains.kotlin.incremental.storage.BasicStringMap
import org.jetbrains.kotlin.incremental.storage.PathStringDescriptor
import java.io.File
import java.util.*

class FileSnapshotMap(storageFile: File) : BasicStringMap<FileSnapshot>(storageFile, PathStringDescriptor, FileSnapshotExternalizer) {
    override fun dumpValue(value: FileSnapshot): String =
            value.toString()

    fun compareAndUpdate(newFiles: Iterable<File>): ChangedFiles.Known {
        val snapshotProvider = SimpleFileSnapshotProviderImpl()
        val newOrModified = ArrayList<File>()
        val removed = ArrayList<File>()

        val newPaths = newFiles.mapTo(HashSet()) { it.canonicalPath }
        for (oldPath in storage.keys) {
            if (oldPath !in newPaths) {
                storage.remove(oldPath)
                removed.add(File(oldPath))
            }
        }

        for (path in newPaths) {
            val file = File(path)
            val oldSnapshot = storage[path]
            val newSnapshot = snapshotProvider[file]

            if (oldSnapshot == null || oldSnapshot != newSnapshot) {
                newOrModified.add(file)
                storage[path] = newSnapshot
            }
        }

        return ChangedFiles.Known(newOrModified, removed)
    }
}