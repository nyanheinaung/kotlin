/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.resolve.diagnostics

import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.js.naming.NameSuggestion
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.resolve.IdentifierChecker

object JsIdentifierChecker : IdentifierChecker {
    override fun checkIdentifier(simpleNameExpression: KtSimpleNameExpression, diagnosticHolder: DiagnosticSink) {
        val simpleName = simpleNameExpression.getReferencedName()

        val hasIllegalChars = simpleName.split('.').any { NameSuggestion.sanitizeName(it) != it }
        if (hasIllegalChars) {
            val identifier = simpleNameExpression.getIdentifier() ?: return
            diagnosticHolder.report(Errors.INVALID_CHARACTERS.on(identifier, "contains illegal characters"))
        }
    }

    override fun checkDeclaration(declaration: KtDeclaration, diagnosticHolder: DiagnosticSink) {}
}
