/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.resolve.calls.callResolverUtil.isInfixCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.VariableAsFunctionResolvedCall
import org.jetbrains.kotlin.resolve.calls.tasks.isDynamic
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameUnsafe
import org.jetbrains.kotlin.types.ErrorUtils

class InfixCallChecker : CallChecker {
    override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
        val functionDescriptor = resolvedCall.resultingDescriptor as? FunctionDescriptor ?: return
        if (functionDescriptor.isInfix || functionDescriptor.isDynamic() || ErrorUtils.isError(functionDescriptor)) return
        val call = ((resolvedCall as? VariableAsFunctionResolvedCall)?.variableCall ?: resolvedCall).call
        if (isInfixCall(call)) {
            val containingDeclarationName = functionDescriptor.containingDeclaration.fqNameUnsafe.asString()
            context.trace.report(Errors.INFIX_MODIFIER_REQUIRED.on(reportOn, functionDescriptor, containingDeclarationName))
        }
    }
}
