/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm.checkers

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtPropertyAccessor
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.inline.InlineUtil
import org.jetbrains.kotlin.resolve.jvm.diagnostics.ErrorsJvm

class ExternalFunChecker : DeclarationChecker {
    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
        if (descriptor !is MemberDescriptor || !descriptor.isExternal) return

        val trace = context.trace
        if (descriptor !is FunctionDescriptor) {
            val target = when (descriptor) {
                is PropertyDescriptor -> "property"
                is ClassDescriptor -> "class"
                else -> "non-function declaration"
            }
            trace.report(Errors.WRONG_MODIFIER_TARGET.on(declaration, KtTokens.EXTERNAL_KEYWORD, target))
            return
        }

        if (DescriptorUtils.isInterface(descriptor.containingDeclaration)) {
            trace.report(ErrorsJvm.EXTERNAL_DECLARATION_IN_INTERFACE.on(declaration))
        }
        else if (descriptor.modality == Modality.ABSTRACT) {
            if (declaration is KtPropertyAccessor) {
                trace.report(ErrorsJvm.EXTERNAL_DECLARATION_CANNOT_BE_ABSTRACT.on(declaration.property))
            }
            else {
                trace.report(ErrorsJvm.EXTERNAL_DECLARATION_CANNOT_BE_ABSTRACT.on(declaration))
            }
        }

        if (descriptor !is ConstructorDescriptor && declaration is KtDeclarationWithBody && declaration.hasBody()) {
            trace.report(ErrorsJvm.EXTERNAL_DECLARATION_CANNOT_HAVE_BODY.on(declaration))
        }

        if (InlineUtil.isInline(descriptor)) {
            trace.report(ErrorsJvm.EXTERNAL_DECLARATION_CANNOT_BE_INLINED.on(declaration))
        }
    }
}
