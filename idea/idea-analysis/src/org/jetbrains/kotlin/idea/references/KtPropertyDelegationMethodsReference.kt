/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.references

import com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptorWithAccessors
import org.jetbrains.kotlin.descriptors.accessors
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.util.OperatorNameConventions

class KtPropertyDelegationMethodsReference(element: KtPropertyDelegate) : KtMultiReference<KtPropertyDelegate>(element) {

    override fun getRangeInElement(): TextRange {
        val byKeywordNode = expression.byKeywordNode
        val offset = byKeywordNode.psi!!.startOffsetInParent
        return TextRange(offset, offset + byKeywordNode.textLength)
    }

    override fun getTargetDescriptors(context: BindingContext): Collection<DeclarationDescriptor> {
        val property = expression.getStrictParentOfType<KtProperty>() ?: return emptyList()
        val descriptor = context[BindingContext.DECLARATION_TO_DESCRIPTOR, property] as? VariableDescriptorWithAccessors ?: return emptyList()
        return (descriptor.accessors.mapNotNull {
            accessor ->
            context.get(BindingContext.DELEGATED_PROPERTY_RESOLVED_CALL, accessor)?.candidateDescriptor
        } + listOfNotNull(context.get(BindingContext.PROVIDE_DELEGATE_RESOLVED_CALL, descriptor)?.candidateDescriptor))
    }

    override val resolvesByNames: Collection<Name> get() = NAMES

    companion object {
        private val NAMES = listOf(OperatorNameConventions.GET_VALUE, OperatorNameConventions.SET_VALUE)
    }
}
