/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types.expressions

import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.parentsWithSelf
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils

class PreliminaryDeclarationVisitor(
    val declaration: KtDeclaration,
    val languageVersionSettings: LanguageVersionSettings
) : AssignedVariablesSearcher() {

    override fun writers(variableDescriptor: VariableDescriptor): MutableSet<Writer> {
        lazyTrigger
        return super.writers(variableDescriptor)
    }

    private val lazyTrigger by lazy {
        declaration.accept(this)
    }

    companion object {

        fun createForExpression(expression: KtExpression, trace: BindingTrace, languageVersionSettings: LanguageVersionSettings) {
            expression.getStrictParentOfType<KtDeclaration>()?.let { createForDeclaration(it, trace, languageVersionSettings) }
        }

        private fun topMostNonClassDeclaration(declaration: KtDeclaration) =
            declaration.parentsWithSelf.filterIsInstance<KtDeclaration>().findLast { it !is KtClassOrObject } ?: declaration

        fun createForDeclaration(declaration: KtDeclaration, trace: BindingTrace, languageVersionSettings: LanguageVersionSettings) {
            val visitorOwner = topMostNonClassDeclaration(declaration)
            if (trace.get(BindingContext.PRELIMINARY_VISITOR, visitorOwner) != null) return
            trace.record(
                BindingContext.PRELIMINARY_VISITOR, visitorOwner,
                PreliminaryDeclarationVisitor(visitorOwner, languageVersionSettings)
            )
        }

        fun getVisitorByVariable(variableDescriptor: VariableDescriptor, bindingContext: BindingContext): PreliminaryDeclarationVisitor? {
            // Search for preliminary visitor of parent descriptor
            val containingDescriptor = variableDescriptor.containingDeclaration
            var currentDeclaration: KtDeclaration? =
                DescriptorToSourceUtils.descriptorToDeclaration(containingDescriptor) as? KtDeclaration ?: return null
            var preliminaryVisitor = bindingContext.get(BindingContext.PRELIMINARY_VISITOR, currentDeclaration)
            while (preliminaryVisitor == null && currentDeclaration != null) {
                currentDeclaration = currentDeclaration.getStrictParentOfType()
                preliminaryVisitor = bindingContext.get(BindingContext.PRELIMINARY_VISITOR, currentDeclaration)
            }
            return preliminaryVisitor
        }
    }
}