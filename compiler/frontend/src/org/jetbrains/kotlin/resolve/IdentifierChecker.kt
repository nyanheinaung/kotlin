/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve

import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtSimpleNameExpression

interface IdentifierChecker {
    fun checkIdentifier(simpleNameExpression: KtSimpleNameExpression, diagnosticHolder: DiagnosticSink)
    fun checkDeclaration(declaration: KtDeclaration, diagnosticHolder: DiagnosticSink)

    object Default : IdentifierChecker {
        override fun checkIdentifier(simpleNameExpression: KtSimpleNameExpression, diagnosticHolder: DiagnosticSink) {}
        override fun checkDeclaration(declaration: KtDeclaration, diagnosticHolder: DiagnosticSink) {}
    }
}
