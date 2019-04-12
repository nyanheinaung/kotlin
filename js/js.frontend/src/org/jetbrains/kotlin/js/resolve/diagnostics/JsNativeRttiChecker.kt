/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.resolve.diagnostics

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.js.translate.utils.AnnotationsUtils
import org.jetbrains.kotlin.psi.KtClassLiteralExpression
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.checkers.RttiExpressionChecker
import org.jetbrains.kotlin.resolve.calls.checkers.RttiExpressionInformation
import org.jetbrains.kotlin.resolve.calls.checkers.RttiOperation
import org.jetbrains.kotlin.resolve.calls.context.ResolutionContext
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.expressions.ClassLiteralChecker

class JsNativeRttiChecker : RttiExpressionChecker, ClassLiteralChecker {
    override fun check(rttiInformation: RttiExpressionInformation, reportOn: PsiElement, trace: BindingTrace) {
        val sourceType = rttiInformation.sourceType
        val targetType = rttiInformation.targetType
        val targetDescriptor = targetType?.constructor?.declarationDescriptor
        if (sourceType != null && targetDescriptor != null && AnnotationsUtils.isNativeInterface(targetDescriptor)) {
            when (rttiInformation.operation) {
                RttiOperation.IS,
                RttiOperation.NOT_IS -> trace.report(ErrorsJs.CANNOT_CHECK_FOR_EXTERNAL_INTERFACE.on(reportOn, targetType))

                RttiOperation.AS,
                RttiOperation.SAFE_AS -> trace.report(ErrorsJs.UNCHECKED_CAST_TO_EXTERNAL_INTERFACE.on(reportOn, sourceType, targetType))
            }
        }
    }

    override fun check(expression: KtClassLiteralExpression, type: KotlinType, context: ResolutionContext<*>) {
        val descriptor = type.constructor.declarationDescriptor as? ClassDescriptor ?: return
        if (AnnotationsUtils.isNativeInterface(descriptor)) {
            context.trace.report(ErrorsJs.EXTERNAL_INTERFACE_AS_CLASS_LITERAL.on(expression))
        }
    }
}
