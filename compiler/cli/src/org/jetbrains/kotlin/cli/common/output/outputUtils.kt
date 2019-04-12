/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.output

import com.intellij.openapi.util.io.FileUtil
import org.jetbrains.kotlin.backend.common.output.OutputFile
import org.jetbrains.kotlin.backend.common.output.OutputFileCollection
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.OutputMessageUtil
import java.io.File

fun OutputFileCollection.writeAll(outputDir: File, report: ((file: OutputFile, sources: List<File>, output: File) -> Unit)?) {
    for (file in asList()) {
        val sources = file.sourceFiles
        val output = File(outputDir, file.relativePath)
        report?.invoke(file, sources, output)
        FileUtil.writeToFile(output, file.asByteArray())
    }
}

fun OutputFileCollection.writeAllTo(outputDir: File) {
    writeAll(outputDir, null)
}

fun OutputFileCollection.writeAll(outputDir: File, messageCollector: MessageCollector, reportOutputFiles: Boolean) {
    if (!reportOutputFiles) writeAllTo(outputDir)
    else writeAll(outputDir) { _, sources, output ->
        messageCollector.report(CompilerMessageSeverity.OUTPUT, OutputMessageUtil.formatOutputMessage(sources, output))
    }
}
