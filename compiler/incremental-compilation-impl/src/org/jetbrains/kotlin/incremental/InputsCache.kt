/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental

import com.intellij.util.containers.MultiMap
import org.jetbrains.kotlin.build.GeneratedFile
import org.jetbrains.kotlin.incremental.snapshots.FileSnapshotMap
import org.jetbrains.kotlin.incremental.storage.*
import java.io.File
import java.util.*
import kotlin.collections.HashSet

class InputsCache(
        workingDir: File,
        private val reporter: ICReporter
) : BasicMapsOwner(workingDir) {
    companion object {
        private val SOURCE_SNAPSHOTS = "source-snapshot"
        private val SOURCE_TO_OUTPUT_FILES = "source-to-output"
    }

    internal val sourceSnapshotMap = registerMap(FileSnapshotMap(SOURCE_SNAPSHOTS.storageFile))
    private val sourceToOutputMap = registerMap(FilesMap(SOURCE_TO_OUTPUT_FILES.storageFile))

    fun removeOutputForSourceFiles(sources: Iterable<File>) {
        for (sourceFile in sources) {
            sourceToOutputMap.remove(sourceFile).forEach { it ->
                reporter.reportVerbose { "Deleting $it on clearing cache for $sourceFile" }
                it.delete()
            }
        }
    }

    // generatedFiles can contain multiple entries with the same source file
    // for example Kapt3 IC will generate a .java stub and .class stub for each source file
    fun registerOutputForSourceFiles(generatedFiles: List<GeneratedFile>) {
        val sourceToOutput = MultiMap<File, File>()

        for (generatedFile in generatedFiles) {
            for (source in generatedFile.sourceFiles) {
                sourceToOutput.putValue(source, generatedFile.outputFile)
            }
        }

        for ((source, outputs) in sourceToOutput.entrySet()) {
            sourceToOutputMap[source] = outputs
        }
    }
}