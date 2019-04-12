/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.config.ApiVersion
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.Call
import org.jetbrains.kotlin.psi.KtArrayAccessExpression
import org.jetbrains.kotlin.psi.KtDestructuringDeclarationEntry
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.resolve.calls.CallTransformer
import org.jetbrains.kotlin.resolve.calls.callResolverUtil.isConventionCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.VariableAsFunctionResolvedCall
import org.jetbrains.kotlin.resolve.calls.tasks.isDynamic
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameUnsafe
import org.jetbrains.kotlin.types.ErrorUtils
import org.jetbrains.kotlin.types.expressions.OperatorConventions

class OperatorCallChecker : CallChecker {
    override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
        val functionDescriptor = resolvedCall.resultingDescriptor as? FunctionDescriptor ?: return
        if (!checkNotErrorOrDynamic(functionDescriptor)) return

        val element = resolvedCall.call.calleeExpression ?: resolvedCall.call.callElement
        val call = resolvedCall.call

        if (resolvedCall is VariableAsFunctionResolvedCall &&
            call is CallTransformer.CallForImplicitInvoke && call.itIsVariableAsFunctionCall) {
            val outerCall = call.outerCall
            if (isConventionCall(outerCall) || isWrongCallWithExplicitTypeArguments(resolvedCall, outerCall)) {
                throw AssertionError(
                    "Illegal resolved call to variable with invoke for $outerCall. " +
                            "Variable: ${resolvedCall.variableCall.resultingDescriptor}" +
                            "Invoke: ${resolvedCall.functionCall.resultingDescriptor}"
                )
            }
        }

        if (call.callElement is KtDestructuringDeclarationEntry || call is CallTransformer.CallForImplicitInvoke) {
            if (!functionDescriptor.isOperator) {
                report(reportOn, functionDescriptor, context.trace)
            }
            return
        }

        val isConventionOperator = element is KtOperationReferenceExpression && element.isConventionOperator()

        if (isConventionOperator) {
            checkModConvention(functionDescriptor, context.languageVersionSettings, context.trace, reportOn)
        }

        if (isConventionOperator || element is KtArrayAccessExpression) {
            if (!functionDescriptor.isOperator) {
                report(reportOn, functionDescriptor, context.trace)
            }
        }
    }

    companion object {
        fun report(reportOn: PsiElement, descriptor: FunctionDescriptor, sink: DiagnosticSink) {
            if (!checkNotErrorOrDynamic(descriptor)) return

            val containingDeclaration = descriptor.containingDeclaration
            val containingDeclarationName = containingDeclaration.fqNameUnsafe.asString()
            sink.report(Errors.OPERATOR_MODIFIER_REQUIRED.on(reportOn, descriptor, containingDeclarationName))
        }

        private fun checkNotErrorOrDynamic(functionDescriptor: FunctionDescriptor): Boolean {
            return !functionDescriptor.isDynamic() && !ErrorUtils.isError(functionDescriptor)
        }

        private fun isWrongCallWithExplicitTypeArguments(
            resolvedCall: VariableAsFunctionResolvedCall,
            outerCall: Call
        ): Boolean {
            val passedTypeArgumentsToInvoke = outerCall.typeArguments.isNotEmpty() &&
                    resolvedCall.functionCall.candidateDescriptor.typeParameters.isNotEmpty()
            return passedTypeArgumentsToInvoke && resolvedCall.variableCall.candidateDescriptor.typeParameters.isNotEmpty()
        }
    }
}

fun FunctionDescriptor.isOperatorMod(): Boolean {
    return this.isOperator && name in OperatorConventions.REM_TO_MOD_OPERATION_NAMES.values
}

fun shouldWarnAboutDeprecatedModFromBuiltIns(languageVersionSettings: LanguageVersionSettings): Boolean {
    return languageVersionSettings.supportsFeature(LanguageFeature.OperatorRem) && languageVersionSettings.apiVersion >= ApiVersion.KOTLIN_1_1
}

private fun checkModConvention(
    descriptor: FunctionDescriptor, languageVersionSettings: LanguageVersionSettings,
    diagnosticHolder: DiagnosticSink, modifier: PsiElement
) {
    if (!descriptor.isOperatorMod()) return

    if (KotlinBuiltIns.isUnderKotlinPackage(descriptor)) {
        if (shouldWarnAboutDeprecatedModFromBuiltIns(languageVersionSettings)) {
            warnAboutDeprecatedOrForbiddenMod(descriptor, diagnosticHolder, modifier, languageVersionSettings)
        }
    } else {
        if (languageVersionSettings.supportsFeature(LanguageFeature.OperatorRem)) {
            warnAboutDeprecatedOrForbiddenMod(descriptor, diagnosticHolder, modifier, languageVersionSettings)
        }
    }
}

private fun warnAboutDeprecatedOrForbiddenMod(
    descriptor: FunctionDescriptor,
    diagnosticHolder: DiagnosticSink,
    reportOn: PsiElement,
    languageVersionSettings: LanguageVersionSettings
) {
    val diagnosticFactory = if (languageVersionSettings.supportsFeature(LanguageFeature.ProhibitOperatorMod))
        Errors.FORBIDDEN_BINARY_MOD_AS_REM
    else
        Errors.DEPRECATED_BINARY_MOD_AS_REM

    val newNameConvention = OperatorConventions.REM_TO_MOD_OPERATION_NAMES.inverse()[descriptor.name]
    diagnosticHolder.report(diagnosticFactory.on(reportOn, descriptor, newNameConvention!!.asString()))
}
