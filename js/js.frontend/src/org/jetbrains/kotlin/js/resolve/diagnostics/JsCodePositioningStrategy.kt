/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.resolve.diagnostics

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.PositioningStrategy
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.diagnostics.DiagnosticWithParameters3
import org.jetbrains.kotlin.diagnostics.ParametrizedDiagnostic
import org.jetbrains.kotlin.js.resolve.diagnostics.JsCallData
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory1
import org.jetbrains.kotlin.diagnostics.DiagnosticWithParameters1

object JsCodePositioningStrategy : PositioningStrategy<PsiElement>() {
    override fun markDiagnostic(diagnostic: ParametrizedDiagnostic<out PsiElement>): List<TextRange> {
        @Suppress("UNCHECKED_CAST")
        val diagnosticWithParameters = diagnostic as DiagnosticWithParameters1<KtExpression, JsCallData>
        val textRange = diagnosticWithParameters.a.reportRange
        return listOf(textRange)
    }
}
