/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.compilerRunner

import org.jetbrains.kotlin.build.GeneratedFile
import org.jetbrains.kotlin.build.GeneratedJvmClass
import java.io.File

data class SimpleOutputItem(val sourceFiles: Collection<File>, val outputFile: File) {
    override fun toString(): String =
            "$sourceFiles->$outputFile"
}

fun SimpleOutputItem.toGeneratedFile(): GeneratedFile =
        when {
            outputFile.name.endsWith(".class") -> GeneratedJvmClass(sourceFiles, outputFile)
            else -> GeneratedFile(sourceFiles, outputFile)
        }