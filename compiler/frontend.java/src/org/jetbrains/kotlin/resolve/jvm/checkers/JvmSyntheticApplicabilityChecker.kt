/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm.checkers

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.jvm.annotations.JVM_SYNTHETIC_ANNOTATION_FQ_NAME
import org.jetbrains.kotlin.resolve.jvm.diagnostics.ErrorsJvm

class JvmSyntheticApplicabilityChecker : DeclarationChecker {
    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
        val annotation = (descriptor as? PropertyDescriptor)?.delegateField?.annotations?.findAnnotation(JVM_SYNTHETIC_ANNOTATION_FQ_NAME)
        if (annotation != null) {
            val annotationEntry = DescriptorToSourceUtils.getSourceFromAnnotation(annotation) ?: return
            context.trace.report(ErrorsJvm.JVM_SYNTHETIC_ON_DELEGATE.on(annotationEntry))
        }
    }
}
