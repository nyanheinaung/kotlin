/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isArrayOfNothing

class CallReturnsArrayOfNothingChecker : CallChecker {
    override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
        if (resolvedCall.resultingDescriptor.returnType.containsArrayOfNothing()) {
            context.trace.report(Errors.UNSUPPORTED.on(reportOn, "Array<Nothing> in return type is illegal"))
        }
    }

    private fun KotlinType?.containsArrayOfNothing(): Boolean {
        if (this == null || isComputingDeferredType(this)) return false

        return isArrayOfNothing() ||
                arguments.any { !it.isStarProjection && it.type.containsArrayOfNothing() }
    }
}
