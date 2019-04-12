/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.resolve.diagnostics

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.MemberDescriptor
import org.jetbrains.kotlin.descriptors.annotations.KotlinRetention
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.descriptorUtil.annotationClass
import org.jetbrains.kotlin.resolve.descriptorUtil.getAnnotationRetention
import org.jetbrains.kotlin.resolve.descriptorUtil.isEffectivelyExternal
import org.jetbrains.kotlin.resolve.source.PsiSourceElement

object JsRuntimeAnnotationChecker : DeclarationChecker {
    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
        for (annotation in descriptor.annotations) {
            val annotationClass = annotation.annotationClass ?: continue
            if (annotationClass.getAnnotationRetention() != KotlinRetention.RUNTIME) continue

            val annotationPsi = (annotation.source as? PsiSourceElement)?.psi ?: declaration

            if (descriptor is MemberDescriptor && descriptor.isEffectivelyExternal()) {
                context.trace.report(ErrorsJs.RUNTIME_ANNOTATION_ON_EXTERNAL_DECLARATION.on(annotationPsi))
            } else {
                context.trace.report(ErrorsJs.RUNTIME_ANNOTATION_NOT_SUPPORTED.on(annotationPsi))
            }
        }
    }
}
