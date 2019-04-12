/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.common.output

import java.io.File

interface OutputFileCollection {
    fun get(relativePath: String): OutputFile?
    fun asList(): List<OutputFile>
}

class SimpleOutputFileCollection(private val outputFiles: List<OutputFile>) : OutputFileCollection {
    override fun get(relativePath: String): OutputFile? = outputFiles.firstOrNull { it.relativePath == relativePath }
    override fun asList(): List<OutputFile> = outputFiles
}

interface OutputFile {
    val relativePath: String
    val sourceFiles: List<File>
    fun asByteArray(): ByteArray
    fun asText(): String
}

class SimpleOutputFile(
        override val sourceFiles: List<File>,
        override val relativePath: String,
        private val content: String
) : OutputFile {
    override fun asByteArray(): ByteArray = content.toByteArray()
    override fun asText(): String = content

    override fun toString() = "$relativePath (compiled from $sourceFiles)"
}

class SimpleOutputBinaryFile(
        override val sourceFiles: List<File>,
        override val relativePath: String,
        private val content: ByteArray
) : OutputFile {
    override fun asByteArray(): ByteArray = content
    override fun asText(): String = String(content)

    override fun toString() = "$relativePath (compiled from $sourceFiles)"
}