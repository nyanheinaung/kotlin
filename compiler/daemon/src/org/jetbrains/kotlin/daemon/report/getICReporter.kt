/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.daemon.report

import org.jetbrains.kotlin.daemon.common.*
import java.io.File
import java.util.*

internal fun getICReporter(
    servicesFacade: CompilerServicesFacadeBase,
    compilationResults: CompilationResults,
    compilationOptions: IncrementalCompilationOptions
): RemoteICReporter {
    val root = compilationOptions.modulesInfo.projectRoot
    val reporters = ArrayList<RemoteICReporter>()

    if (ReportCategory.IC_MESSAGE.code in compilationOptions.reportCategories) {
        val isVerbose = compilationOptions.reportSeverity == ReportSeverity.DEBUG.code
        reporters.add(DebugMessagesICReporter(servicesFacade, root, isVerbose = isVerbose))
    }

    val requestedResults = compilationOptions
        .requestedCompilationResults
        .mapNotNullTo(HashSet()) { resultCode ->
            CompilationResultCategory.values().getOrNull(resultCode)
        }
    requestedResults.mapTo(reporters) { requestedResult ->
        when (requestedResult) {
            CompilationResultCategory.IC_COMPILE_ITERATION -> {
                CompileIterationICReporter(compilationResults)
            }
            CompilationResultCategory.BUILD_REPORT_LINES -> {
                BuildReportICReporter(compilationResults, root)
            }
            CompilationResultCategory.VERBOSE_BUILD_REPORT_LINES -> {
                BuildReportICReporter(compilationResults, root, isVerbose = true)
            }
        }
    }

    return CompositeICReporter(reporters)
}


