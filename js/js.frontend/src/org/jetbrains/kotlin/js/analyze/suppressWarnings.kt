/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.analyze

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.js.PredefinedAnnotation.*
import org.jetbrains.kotlin.resolve.checkers.PlatformDiagnosticSuppressor

private val NATIVE_ANNOTATIONS = arrayOf(NATIVE.fqName, NATIVE_INVOKE.fqName, NATIVE_GETTER.fqName, NATIVE_SETTER.fqName)

private fun DeclarationDescriptor.isLexicallyInsideJsNative(): Boolean {
    var descriptor: DeclarationDescriptor = this
    while (true) {
        val annotations = descriptor.annotations
        if (!annotations.isEmpty() && NATIVE_ANNOTATIONS.any(annotations::hasAnnotation)) return true
        descriptor = descriptor.containingDeclaration ?: break
    }
    return false
}

object JsNativeDiagnosticSuppressor : PlatformDiagnosticSuppressor {
    override fun shouldReportUnusedParameter(parameter: VariableDescriptor): Boolean = !parameter.isLexicallyInsideJsNative()

    override fun shouldReportNoBody(descriptor: CallableMemberDescriptor): Boolean = !descriptor.isLexicallyInsideJsNative()
}
