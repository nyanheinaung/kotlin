/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm.checkers

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.jvm.annotations.findStrictfpAnnotation
import org.jetbrains.kotlin.resolve.jvm.diagnostics.ErrorsJvm

class StrictfpApplicabilityChecker : DeclarationChecker {
    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
        val annotation = descriptor.findStrictfpAnnotation() ?: return
        if (declaration is KtClassOrObject && descriptor is ClassDescriptor) {
            val annotationEntry = DescriptorToSourceUtils.getSourceFromAnnotation(annotation) ?: return
            context.trace.report(ErrorsJvm.STRICTFP_ON_CLASS.on(annotationEntry))
        }
    }
}
