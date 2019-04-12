/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.highlighter

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.extensions.Extensions
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.utils.addToStdlib.firstNotNullResult

abstract class AfterAnalysisHighlightingVisitor protected constructor(
    holder: AnnotationHolder, protected var bindingContext: BindingContext
) : HighlightingVisitor(holder) {

    protected fun attributeKeyForDeclarationFromExtensions(element: PsiElement, descriptor: DeclarationDescriptor) =
        Extensions.getExtensions(HighlighterExtension.EP_NAME).firstNotNullResult { extension ->
            extension.highlightDeclaration(element, descriptor)
        }

    protected fun attributeKeyForCallFromExtensions(
        expression: KtSimpleNameExpression,
        resolvedCall: ResolvedCall<out CallableDescriptor>
    ): TextAttributesKey? {
        return Extensions.getExtensions(HighlighterExtension.EP_NAME).firstNotNullResult { extension ->
            extension.highlightCall(expression, resolvedCall)
        }
    }
}
