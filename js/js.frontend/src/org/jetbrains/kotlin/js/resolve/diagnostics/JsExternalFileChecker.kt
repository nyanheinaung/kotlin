/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.resolve.diagnostics

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.js.translate.utils.AnnotationsUtils
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker

object JsExternalFileChecker : DeclarationChecker {
    private val annotationFqNames = setOf(AnnotationsUtils.JS_MODULE_ANNOTATION, AnnotationsUtils.JS_QUALIFIER_ANNOTATION)

    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
        if (!AnnotationsUtils.isNativeObject(descriptor) && DescriptorUtils.isTopLevelDeclaration(descriptor)) {
            AnnotationsUtils.getContainingFileAnnotations(context.trace.bindingContext, descriptor).asSequence()
                .firstOrNull { it.fqName in annotationFqNames }
                ?.let {
                    context.trace.report(ErrorsJs.NON_EXTERNAL_DECLARATION_IN_INAPPROPRIATE_FILE.on(declaration, it.type))
                }
        }
    }
}
