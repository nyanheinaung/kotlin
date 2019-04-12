/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.descriptors.impl.FunctionExpressionDescriptor
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.*

object UnderscoreChecker : DeclarationChecker {
    @JvmOverloads
    fun checkIdentifier(
        identifier: PsiElement?,
        diagnosticHolder: DiagnosticSink,
        languageVersionSettings: LanguageVersionSettings,
        allowSingleUnderscore: Boolean = false
    ) {
        if (identifier == null || identifier.text.isEmpty()) return
        val isValidSingleUnderscore = allowSingleUnderscore && identifier.text == "_"
        if (!isValidSingleUnderscore && identifier.text.all { it == '_' }) {
            diagnosticHolder.report(Errors.UNDERSCORE_IS_RESERVED.on(identifier))
        } else if (isValidSingleUnderscore && !languageVersionSettings.supportsFeature(LanguageFeature.SingleUnderscoreForParameterName)) {
            diagnosticHolder.report(
                Errors.UNSUPPORTED_FEATURE.on(
                    identifier,
                    LanguageFeature.SingleUnderscoreForParameterName to languageVersionSettings
                )
            )
        }
    }

    @JvmOverloads
    fun checkNamed(
        declaration: KtNamedDeclaration,
        diagnosticHolder: DiagnosticSink,
        languageVersionSettings: LanguageVersionSettings,
        allowSingleUnderscore: Boolean = false
    ) {
        checkIdentifier(declaration.nameIdentifier, diagnosticHolder, languageVersionSettings, allowSingleUnderscore)
    }

    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
        if (declaration is KtProperty && descriptor !is VariableDescriptor) return
        if (declaration is KtCallableDeclaration) {
            for (parameter in declaration.valueParameters) {
                checkNamed(
                    parameter, context.trace, context.languageVersionSettings,
                    allowSingleUnderscore = descriptor is FunctionExpressionDescriptor
                )
            }
        }
        if (declaration is KtTypeParameterListOwner) {
            for (typeParameter in declaration.typeParameters) {
                checkNamed(typeParameter, context.trace, context.languageVersionSettings)
            }
        }
        if (declaration !is KtNamedDeclaration) return
        checkNamed(declaration, context.trace, context.languageVersionSettings)
    }
}
