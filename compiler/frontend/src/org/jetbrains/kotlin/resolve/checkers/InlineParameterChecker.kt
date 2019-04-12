/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.checkers

import org.jetbrains.kotlin.builtins.isBuiltinFunctionalType
import org.jetbrains.kotlin.builtins.isSuspendFunctionType
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken
import org.jetbrains.kotlin.lexer.KtToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.resolve.BindingContext

object InlineParameterChecker : DeclarationChecker {
    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
        if (declaration is KtFunction) {
            val inline = declaration.hasModifier(KtTokens.INLINE_KEYWORD)
            for (parameter in declaration.valueParameters) {
                val parameterDescriptor = context.trace.get(BindingContext.VALUE_PARAMETER, parameter)
                if (!inline || (parameterDescriptor != null && !parameterDescriptor.type.isBuiltinFunctionalType)) {
                    parameter.reportIncorrectInline(KtTokens.NOINLINE_KEYWORD, context.trace)
                    parameter.reportIncorrectInline(KtTokens.CROSSINLINE_KEYWORD, context.trace)
                }
                if (inline && !parameter.hasModifier(KtTokens.NOINLINE_KEYWORD) &&
                    !parameter.hasModifier(KtTokens.CROSSINLINE_KEYWORD) &&
                    parameterDescriptor?.type?.isSuspendFunctionType == true
                ) {
                    if (declaration.hasModifier(KtTokens.SUSPEND_KEYWORD)) {
                        val typeReference = parameter.typeReference!!
                        val modifierList = typeReference.modifierList!!
                        val modifier = modifierList.getModifier(KtTokens.SUSPEND_KEYWORD)!!
                        context.trace.report(Errors.REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE.on(modifier))
                    } else {
                        context.trace.report(Errors.INLINE_SUSPEND_FUNCTION_TYPE_UNSUPPORTED.on(parameter))
                    }
                }
            }
        }
    }

    private fun KtParameter.reportIncorrectInline(modifierToken: KtModifierKeywordToken, diagnosticHolder: DiagnosticSink) {
        val modifier = modifierList?.getModifier(modifierToken)
        modifier?.let {
            diagnosticHolder.report(Errors.ILLEGAL_INLINE_PARAMETER_MODIFIER.on(modifier, modifierToken))
        }
    }
}
