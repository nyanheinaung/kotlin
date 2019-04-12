/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.search.usagesSearch.operators

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.search.SearchRequestCollector
import com.intellij.psi.search.SearchScope
import org.jetbrains.kotlin.compatibility.ExecutorProcessor
import org.jetbrains.kotlin.idea.references.KtPropertyDelegationMethodsReference
import org.jetbrains.kotlin.idea.search.ideaExtensions.KotlinReferencesSearchOptions
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance

class PropertyDelegationOperatorReferenceSearcher(
    targetFunction: PsiElement,
    searchScope: SearchScope,
    consumer: ExecutorProcessor<PsiReference>,
    optimizer: SearchRequestCollector,
    options: KotlinReferencesSearchOptions
) : OperatorReferenceSearcher<KtPropertyDelegate>(targetFunction, searchScope, consumer, optimizer, options, wordsToSearch = listOf("by")) {

    override fun processPossibleReceiverExpression(expression: KtExpression) {
        (expression.parent as? KtPropertyDelegate)?.let { processReferenceElement(it) }
    }

    override fun isReferenceToCheck(ref: PsiReference): Boolean {
        return ref is KtPropertyDelegationMethodsReference
    }

    override fun extractReference(element: KtElement): PsiReference? {
        return (element as? KtPropertyDelegate)?.references?.firstIsInstance<KtPropertyDelegationMethodsReference>()
    }
}