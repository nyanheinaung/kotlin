/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin

import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.xml.XmlAttributeValue
import org.jetbrains.kotlin.compatibility.ExecutorProcessor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.idea.caches.resolve.resolveToCall
import org.jetbrains.kotlin.idea.references.KtSimpleNameReference
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.resolve.source.getPsi


class AndroidExtensionsReferenceSearchExecutor : QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters>(true) {
    override fun processQuery(queryParameters: ReferencesSearch.SearchParameters, consumer: ExecutorProcessor<PsiReference>) {
        val elementToSearch = queryParameters.elementToSearch as? XmlAttributeValue ?: return
        val scopeElements = (queryParameters.effectiveSearchScope as? LocalSearchScope)?.scope ?: return
        val referenceName = elementToSearch.value?.substringAfterLast("/") ?: return

        scopeElements.filterIsInstance<KtElement>().forEach {
            it.accept(object : KtTreeVisitorVoid() {
                override fun visitSimpleNameExpression(expression: KtSimpleNameExpression) {
                    if (expression.text == referenceName && expression.isReferenceTo(elementToSearch)) {
                        expression.references.firstOrNull { it is KtSimpleNameReference }?.let {
                            consumer.process(it)
                        }
                    }
                    super.visitReferenceExpression(expression)
                }
            })
        }
    }

    private fun KtReferenceExpression.isReferenceTo(element: PsiElement): Boolean =
            getTargetPropertyDescriptor()?.source?.getPsi() == element

    private fun KtReferenceExpression.getTargetPropertyDescriptor(): PropertyDescriptor? =
            resolveToCall()?.resultingDescriptor as? PropertyDescriptor
}