/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.messages

import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils

/**
 * This class behaviour is the same as [MessageCollector.report] in [AnalyzerWithCompilerReport.reportDiagnostic].
 */
class DefaultDiagnosticReporter(override val messageCollector: MessageCollector) : MessageCollectorBasedReporter

interface MessageCollectorBasedReporter : DiagnosticMessageReporter {
    val messageCollector: MessageCollector

    override fun report(diagnostic: Diagnostic, file: PsiFile, render: String) = messageCollector.report(
        AnalyzerWithCompilerReport.convertSeverity(diagnostic.severity),
        render,
        MessageUtil.psiFileToMessageLocation(file, file.name, DiagnosticUtils.getLineAndColumn(diagnostic))
    )
}
