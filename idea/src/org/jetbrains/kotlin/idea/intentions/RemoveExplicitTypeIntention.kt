/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.idea.refactoring.addTypeArgumentsIfNeeded
import org.jetbrains.kotlin.idea.refactoring.getQualifiedTypeArgumentList
import org.jetbrains.kotlin.idea.search.usagesSearch.descriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import org.jetbrains.kotlin.types.typeUtil.isUnit

class RemoveExplicitTypeIntention : SelfTargetingRangeIntention<KtCallableDeclaration>(
    KtCallableDeclaration::class.java,
    "Remove explicit type specification"
), HighPriorityAction {

    override fun applicabilityRange(element: KtCallableDeclaration): TextRange? {
        return getRange(element)
    }

    override fun applyTo(element: KtCallableDeclaration, editor: Editor?) {
        val initializer = (element as? KtProperty)?.initializer
        val typeArgumentList = initializer?.let { getQualifiedTypeArgumentList(it) }
        element.typeReference = null
        if (typeArgumentList != null) addTypeArgumentsIfNeeded(initializer, typeArgumentList)
    }

    companion object {
        fun getRange(element: KtCallableDeclaration): TextRange? {
            if (element.containingFile is KtCodeFragment) return null
            val typeReference = element.typeReference ?: return null
            if (typeReference.annotationEntries.isNotEmpty()) return null

            if (element is KtParameter) {
                if (element.isLoopParameter) return element.textRange
                if (element.isSetterParameter) return typeReference.textRange
            }

            if (element !is KtProperty && element !is KtNamedFunction) return null

            if (element is KtNamedFunction
                && element.hasBlockBody()
                && (element.descriptor as? FunctionDescriptor)?.returnType?.isUnit()?.not() != false
            ) return null

            val initializer = (element as? KtDeclarationWithInitializer)?.initializer

            if (!redundantTypeSpecification(element, initializer)) return null

            return when {
                initializer != null -> TextRange(element.startOffset, initializer.startOffset - 1)
                element is KtProperty && element.getter != null -> TextRange(element.startOffset, typeReference.endOffset)
                element is KtNamedFunction -> TextRange(element.startOffset, typeReference.endOffset)
                else -> null
            }
        }

        fun redundantTypeSpecification(element: KtCallableDeclaration, initializer: KtExpression?): Boolean {
            if (initializer == null) return true
            if (initializer !is KtLambdaExpression && initializer !is KtNamedFunction) return true
            val functionType = element.typeReference?.typeElement as? KtFunctionType ?: return true
            if (functionType.receiver != null) return false
            if (functionType.parameters.isEmpty()) return true
            val valueParameters = when (initializer) {
                is KtLambdaExpression -> initializer.valueParameters
                is KtNamedFunction -> initializer.valueParameters
                else -> emptyList()
            }
            return valueParameters.isNotEmpty() && valueParameters.none { it.typeReference == null }
        }
    }
}

internal val KtParameter.isSetterParameter: Boolean get() = (parent.parent as? KtPropertyAccessor)?.isSetter ?: false