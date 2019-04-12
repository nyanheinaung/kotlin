/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.IdentifierChecker

object JvmSimpleNameBacktickChecker : IdentifierChecker {
    // See The Java Virtual Machine Specification, section 4.7.9.1 https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.9.1
    private val CHARS = setOf('.', ';', '[', ']', '/', '<', '>', ':', '\\')

    override fun checkIdentifier(simpleNameExpression: KtSimpleNameExpression, diagnosticHolder: DiagnosticSink) {
        reportIfNeeded(simpleNameExpression.getReferencedName(), { simpleNameExpression.getIdentifier() }, diagnosticHolder)
    }

    override fun checkDeclaration(declaration: KtDeclaration, diagnosticHolder: DiagnosticSink) {
        if (declaration is KtDestructuringDeclaration) {
            declaration.entries.forEach { checkNamed(it, diagnosticHolder) }
        }
        if (declaration is KtCallableDeclaration) {
            declaration.valueParameters.forEach { checkNamed(it, diagnosticHolder) }
        }
        if (declaration is KtTypeParameterListOwner) {
            declaration.typeParameters.forEach { checkNamed(it, diagnosticHolder) }
        }
        if (declaration is KtNamedDeclaration) {
            checkNamed(declaration, diagnosticHolder)
        }
    }

    private fun checkNamed(declaration: KtNamedDeclaration, diagnosticHolder: DiagnosticSink) {
        val name = declaration.name ?: return

        reportIfNeeded(name, { declaration.nameIdentifier ?: declaration }, diagnosticHolder)
    }

    private fun reportIfNeeded(name: String, reportOn: () -> PsiElement?, diagnosticHolder: DiagnosticSink) {
        val text = KtPsiUtil.unquoteIdentifier(name)
        if (text.isEmpty()) {
            diagnosticHolder.report(Errors.INVALID_CHARACTERS.on(reportOn() ?: return, "should not be empty"))
        } else if (text.any { it in CHARS }) {
            diagnosticHolder.report(
                Errors.INVALID_CHARACTERS.on(
                    reportOn() ?: return,
                    "contains illegal characters: ${CHARS.intersect(text.toSet()).joinToString(
                        ""
                    )}"
                )
            )
        }
    }
}

