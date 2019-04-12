/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.resolve.diagnostics

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.js.translate.utils.AnnotationsUtils
import org.jetbrains.kotlin.resolve.calls.checkers.CallChecker
import org.jetbrains.kotlin.resolve.calls.checkers.CallCheckerContext
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall

class JsReifiedNativeChecker : CallChecker {
    override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
        val typeArgumentList = resolvedCall.call.typeArgumentList?.arguments
        for ((typeParam, typeArg) in resolvedCall.typeArguments) {
            if (!typeParam.isReified) continue

            val typeArgDescriptor = typeArg.constructor.declarationDescriptor
            if (typeArgDescriptor != null && AnnotationsUtils.isNativeInterface(typeArgDescriptor)) {
                val typeArgumentPsi = if (typeArgumentList != null) {
                    typeArgumentList[typeParam.index].typeReference
                }
                else {
                    resolvedCall.call.callElement
                }

                context.trace.report(ErrorsJs.EXTERNAL_INTERFACE_AS_REIFIED_TYPE_ARGUMENT.on(typeArgumentPsi!!, typeArg))
            }
        }
    }
}