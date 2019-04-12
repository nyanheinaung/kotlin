/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.calls.tasks.ExplicitReceiverKind

class SafeCallChecker : CallChecker {
    override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
        val operationNode = resolvedCall.call.callOperationNode ?: return

        if (operationNode.elementType == KtTokens.SAFE_ACCESS &&
            resolvedCall.explicitReceiverKind == ExplicitReceiverKind.NO_EXPLICIT_RECEIVER) {
            context.trace.report(Errors.UNEXPECTED_SAFE_CALL.on(operationNode.psi))
        }
    }
}
