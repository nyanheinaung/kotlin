/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.Errors.UNSUPPORTED
import org.jetbrains.kotlin.resolve.calls.callUtil.isCallableReference
import org.jetbrains.kotlin.resolve.calls.checkers.CallChecker
import org.jetbrains.kotlin.resolve.calls.checkers.CallCheckerContext
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.synthetic.SyntheticJavaPropertyDescriptor

class UnsupportedSyntheticCallableReferenceChecker : CallChecker {
    override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
        // TODO: support references to synthetic Java extension properties (KT-8575)
        if (resolvedCall.call.isCallableReference() && resolvedCall.resultingDescriptor is SyntheticJavaPropertyDescriptor) {
            context.trace.report(UNSUPPORTED.on(reportOn, "reference to the synthetic extension property for a Java get/set method"))
        }
    }
}
