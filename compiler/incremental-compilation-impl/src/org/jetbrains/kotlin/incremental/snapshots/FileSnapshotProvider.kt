/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental.snapshots

import java.io.File

interface FileSnapshotProvider {
    operator fun get(file: File): FileSnapshot
}

class SimpleFileSnapshotProviderImpl : FileSnapshotProvider {
    override fun get(file: File): FileSnapshot {
        val length = file.length()
        val hash = file.md5
        return FileSnapshot(file, length, hash)
    }
}