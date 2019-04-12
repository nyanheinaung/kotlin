/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.resolve.calls.callResolverUtil.getSuperCallExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.usesDefaultArguments
import org.jetbrains.kotlin.resolve.calls.checkers.CallChecker
import org.jetbrains.kotlin.resolve.calls.checkers.CallCheckerContext
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.jvm.diagnostics.ErrorsJvm

class SuperCallWithDefaultArgumentsChecker : CallChecker {
    override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
        val superCallExpression = getSuperCallExpression(resolvedCall.call)
        if (superCallExpression == null || !resolvedCall.usesDefaultArguments()) return
        context.trace.report(ErrorsJvm.SUPER_CALL_WITH_DEFAULT_PARAMETERS.on(reportOn, resolvedCall.resultingDescriptor.name.asString()))
    }
}
