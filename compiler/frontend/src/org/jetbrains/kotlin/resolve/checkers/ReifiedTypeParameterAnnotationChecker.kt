/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.checkers

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils
import org.jetbrains.kotlin.resolve.inline.InlineUtil

class ReifiedTypeParameterAnnotationChecker : DeclarationChecker {
    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
        if (descriptor is CallableDescriptor &&
            !(InlineUtil.isInline(descriptor) || InlineUtil.isPropertyWithAllAccessorsAreInline(descriptor))) {
            checkTypeParameterDescriptorsAreNotReified(descriptor.typeParameters, context.trace)
        }

        if (descriptor is ClassDescriptor) {
            checkTypeParameterDescriptorsAreNotReified(descriptor.declaredTypeParameters, context.trace)
        }
    }


    private fun checkTypeParameterDescriptorsAreNotReified(
        typeParameterDescriptors: List<TypeParameterDescriptor>,
        diagnosticHolder: DiagnosticSink
    ) {
        for (reifiedTypeParameterDescriptor in typeParameterDescriptors.filter { it.isReified }) {
            val typeParameterDeclaration = DescriptorToSourceUtils.descriptorToDeclaration(reifiedTypeParameterDescriptor)
            if (typeParameterDeclaration !is KtTypeParameter) throw AssertionError("JetTypeParameter expected")

            diagnosticHolder.report(
                Errors.REIFIED_TYPE_PARAMETER_NO_INLINE.on(
                    typeParameterDeclaration.modifierList!!.getModifier(KtTokens.REIFIED_KEYWORD)!!
                )
            )
        }
    }
}
