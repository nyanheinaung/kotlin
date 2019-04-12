/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.diagnostics.Errors.API_NOT_AVAILABLE
import org.jetbrains.kotlin.resolve.SinceKotlinAccessibility
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.calls.util.FakeCallableDescriptorForObject
import org.jetbrains.kotlin.resolve.checkSinceKotlinVersionAccessibility

// TODO: consider combining with DeprecatedCallChecker somehow
object ApiVersionCallChecker : CallChecker {
    override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
        check(resolvedCall.resultingDescriptor, context, reportOn)
    }

    private fun check(targetDescriptor: CallableDescriptor, context: CallCheckerContext, element: PsiElement) {
        // Objects will be checked by ApiVersionClassifierUsageChecker
        if (targetDescriptor is FakeCallableDescriptorForObject) return

        val accessibility = targetDescriptor.checkSinceKotlinVersionAccessibility(context.languageVersionSettings)
        if (accessibility is SinceKotlinAccessibility.NotAccessible) {
            context.trace.report(
                API_NOT_AVAILABLE.on(element, accessibility.version.versionString, context.languageVersionSettings.apiVersion.versionString)
            )
        }

        if (accessibility == SinceKotlinAccessibility.Accessible &&
            targetDescriptor is PropertyDescriptor && DeprecatedCallChecker.shouldCheckPropertyGetter(element)) {
            targetDescriptor.getter?.let { check(it, context, element) }
        }
    }
}
