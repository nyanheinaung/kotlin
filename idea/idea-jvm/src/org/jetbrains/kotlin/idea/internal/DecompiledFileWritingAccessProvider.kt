/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.internal

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.WritingAccessProvider
import com.intellij.openapi.vfs.ex.dummy.DummyFileSystem

class DecompiledFileWritingAccessProvider : WritingAccessProvider() {
    override fun isPotentiallyWritable(file: VirtualFile): Boolean {
        if (file.fileSystem is DummyFileSystem && file.parent?.name == KOTLIN_DECOMPILED_FOLDER) {
            return false
        }
        return true
    }

    override fun requestWriting(vararg files: VirtualFile): Collection<VirtualFile> = emptyList()
}