/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.callUtil.isCallableReference
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall

class CallableReferenceCompatibilityChecker : CallChecker {
    override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
        val typeInferenceForCallableReferencesFeature = LanguageFeature.TypeInferenceOnGenericsForCallableReferences
        if (context.languageVersionSettings.supportsFeature(typeInferenceForCallableReferencesFeature)) return

        for ((_, resolvedArgument) in resolvedCall.valueArguments) {
            inner@ for (argument in resolvedArgument.arguments) {
                val argumentExpression = argument.getArgumentExpression() as? KtCallableReferenceExpression ?: continue@inner
                val callableReferenceResolvedCall =
                    argumentExpression.callableReference.getResolvedCall(context.trace.bindingContext) ?: continue@inner
                if (callableReferenceResolvedCall.call.isCallableReference() &&
                    callableReferenceResolvedCall.candidateDescriptor.typeParameters.isNotEmpty()) {
                    context.trace.report(
                        Errors.UNSUPPORTED_FEATURE.on(
                            argumentExpression,
                            typeInferenceForCallableReferencesFeature to context.languageVersionSettings
                        )
                    )
                }
            }
        }
    }
}