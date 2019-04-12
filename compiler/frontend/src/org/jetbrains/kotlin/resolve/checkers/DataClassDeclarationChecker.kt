/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.checkers

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration

class DataClassDeclarationChecker : DeclarationChecker {
    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
        if (descriptor !is ClassDescriptor) return
        if (declaration !is KtClassOrObject) return

        if (descriptor.isData) {
            if (descriptor.unsubstitutedPrimaryConstructor == null && descriptor.constructors.isNotEmpty()) {
                declaration.nameIdentifier?.let { context.trace.report(Errors.PRIMARY_CONSTRUCTOR_REQUIRED_FOR_DATA_CLASS.on(it)) }
            }
            val primaryConstructor = declaration.primaryConstructor
            val parameters = primaryConstructor?.valueParameters ?: emptyList()
            if (parameters.isEmpty()) {
                (primaryConstructor?.valueParameterList ?: declaration.nameIdentifier)?.let {
                    context.trace.report(Errors.DATA_CLASS_WITHOUT_PARAMETERS.on(it))
                }
            }
            for (parameter in parameters) {
                if (parameter.isVarArg) {
                    context.trace.report(Errors.DATA_CLASS_VARARG_PARAMETER.on(parameter))
                }
                if (!parameter.hasValOrVar()) {
                    context.trace.report(Errors.DATA_CLASS_NOT_PROPERTY_PARAMETER.on(parameter))
                }
            }
        }
    }
}
