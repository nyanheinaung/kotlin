/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.resolve.deprecation.createDeprecationDiagnostic

class DeprecatedClassifierUsageChecker : ClassifierUsageChecker {
    override fun check(targetDescriptor: ClassifierDescriptor, element: PsiElement, context: ClassifierUsageCheckerContext) {
        if (element.parent is KtThisExpression) return

        for (deprecation in context.deprecationResolver.getDeprecations(targetDescriptor)) {
            context.trace.report(createDeprecationDiagnostic(element, deprecation, context.languageVersionSettings))
        }
    }
}
