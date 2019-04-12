/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.build

import org.jetbrains.kotlin.incremental.LocalFileKotlinClass
import org.jetbrains.kotlin.metadata.jvm.deserialization.ModuleMapping
import org.jetbrains.kotlin.utils.sure
import java.io.File

open class GeneratedFile(
    val sourceFiles: Collection<File>,
    val outputFile: File
)

class GeneratedJvmClass (
        sourceFiles: Collection<File>,
        outputFile: File
) : GeneratedFile(sourceFiles, outputFile) {
    val outputClass = LocalFileKotlinClass.create(outputFile).sure {
        "Couldn't load KotlinClass from $outputFile; it may happen because class doesn't have valid Kotlin annotations"
    }
}

fun File.isModuleMappingFile() = extension == ModuleMapping.MAPPING_FILE_EXT && parentFile.name == "META-INF"
