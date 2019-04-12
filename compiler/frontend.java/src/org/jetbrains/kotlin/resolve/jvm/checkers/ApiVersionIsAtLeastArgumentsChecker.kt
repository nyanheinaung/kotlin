/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.resolve.calls.checkers.CallChecker
import org.jetbrains.kotlin.resolve.calls.checkers.CallCheckerContext
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.jvm.diagnostics.ErrorsJvm
import org.jetbrains.kotlin.resolve.jvm.getCompileTimeConstant

object ApiVersionIsAtLeastArgumentsChecker : CallChecker {
    override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
        if (!isApiVersionIsAtLeast(resolvedCall.resultingDescriptor)) return

        val bindingContext = context.trace.bindingContext
        val shouldInlineConstVals = context.languageVersionSettings.supportsFeature(LanguageFeature.InlineConstVals)

        for ((_, resolvedValueArgument) in resolvedCall.valueArguments) {
            for (valueArgument in resolvedValueArgument.arguments) {
                val ktExpression =  KtPsiUtil.deparenthesize(valueArgument.getArgumentExpression() ?: continue) ?: continue

                val constant = getCompileTimeConstant(ktExpression, bindingContext, false, shouldInlineConstVals)
                if (constant == null) {
                    context.trace.report(ErrorsJvm.API_VERSION_IS_AT_LEAST_ARGUMENT_SHOULD_BE_CONSTANT.on(ktExpression))
                }
            }
        }
    }

    private fun isApiVersionIsAtLeast(descriptor: CallableDescriptor): Boolean {
        val functionDescriptor = descriptor as? FunctionDescriptor ?: return false

        if (functionDescriptor.name.asString() != "apiVersionIsAtLeast") return false

        val returnType = functionDescriptor.returnType ?: return false
        if (!KotlinBuiltIns.isBoolean(returnType)) return false

        if (!functionDescriptor.valueParameters.all { KotlinBuiltIns.isInt(it.type) }) return false

        val containingPackage = functionDescriptor.containingDeclaration as? PackageFragmentDescriptor ?: return false
        return containingPackage.fqName.asString() == "kotlin.internal"
    }
}