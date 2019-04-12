/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.resolve.diagnostics

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.js.resolve.diagnostics.JsExternalChecker.DEFINED_EXTERNALLY_PROPERTY_NAMES
import org.jetbrains.kotlin.js.translate.utils.AnnotationsUtils
import org.jetbrains.kotlin.resolve.calls.checkers.CallChecker
import org.jetbrains.kotlin.resolve.calls.checkers.CallCheckerContext
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameUnsafe

object JsDefinedExternallyCallChecker : CallChecker {
    override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
        if (resolvedCall.resultingDescriptor.fqNameUnsafe !in DEFINED_EXTERNALLY_PROPERTY_NAMES) return

        val ownerDescriptor = context.scope.ownerDescriptor
        if (!AnnotationsUtils.isNativeObject(ownerDescriptor) && !AnnotationsUtils.isPredefinedObject(ownerDescriptor)) {
            context.trace.report(ErrorsJs.CALL_TO_DEFINED_EXTERNALLY_FROM_NON_EXTERNAL_DECLARATION.on(reportOn))
        }
    }
}
