/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.resolve.SinceKotlinAccessibility
import org.jetbrains.kotlin.resolve.checkSinceKotlinVersionAccessibility

object ApiVersionClassifierUsageChecker : ClassifierUsageChecker {
    override fun check(targetDescriptor: ClassifierDescriptor, element: PsiElement, context: ClassifierUsageCheckerContext) {
        val accessibility = targetDescriptor.checkSinceKotlinVersionAccessibility(context.languageVersionSettings)
        if (accessibility is SinceKotlinAccessibility.NotAccessible) {
            context.trace.report(
                Errors.API_NOT_AVAILABLE.on(
                    element, accessibility.version.versionString, context.languageVersionSettings.apiVersion.versionString
                )
            )
        }
    }
}
