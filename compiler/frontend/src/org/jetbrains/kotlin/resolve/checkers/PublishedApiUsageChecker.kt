/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.checkers

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithVisibility
import org.jetbrains.kotlin.descriptors.PropertyAccessorDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace

object PublishedApiUsageChecker {
    fun check(
        declaration: KtDeclaration,
        descriptor: DeclarationDescriptor,
        trace: BindingTrace
    ) {
        if (descriptor !is DeclarationDescriptorWithVisibility || descriptor.visibility == Visibilities.INTERNAL) return
        // Don't report the diagnostic twice
        if (descriptor is PropertyAccessorDescriptor) return

        for (entry in declaration.annotationEntries) {
            val annotationDescriptor = trace.get(BindingContext.ANNOTATION, entry) ?: continue
            if (annotationDescriptor.fqName == KotlinBuiltIns.FQ_NAMES.publishedApi) {
                trace.report(Errors.NON_INTERNAL_PUBLISHED_API.on(entry))
            }
        }
    }
}
