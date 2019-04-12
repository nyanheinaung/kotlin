/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.dce

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.zip.ZipFile

class InputResource(val name: String, val lastModified: () -> Long, val reader: () -> InputStream) {
    companion object {
        fun file(path: String): InputResource = InputResource(path, { File(path).lastModified() }) { FileInputStream(File(path)) }

        fun zipFile(path: String, entryPath: String): InputResource =
                InputResource("$path!$entryPath", { getZipModificationTime(path, entryPath) }) {
                    val zipFile = ZipFile(path)
                    zipFile.getInputStream(zipFile.getEntry(entryPath))
                }

        private fun getZipModificationTime(path: String, entryPath: String): Long {
            val result = ZipFile(path).getEntry(entryPath).time
            return if (result != -1L) result else File(path).lastModified()
        }
    }
}