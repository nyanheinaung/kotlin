/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.search.ideaExtensions

import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.psi.PsiReference
import com.intellij.psi.search.searches.MethodReferencesSearch
import org.jetbrains.kotlin.compatibility.ExecutorProcessor
import org.jetbrains.kotlin.idea.caches.resolve.util.hasJavaResolutionFacade
import org.jetbrains.kotlin.idea.search.usagesSearch.operators.OperatorReferenceSearcher

class KotlinConventionMethodReferencesSearcher : QueryExecutorBase<PsiReference, MethodReferencesSearch.SearchParameters>(true) {
    override fun processQuery(queryParameters: MethodReferencesSearch.SearchParameters, consumer: ExecutorProcessor<PsiReference>) {
        val method = queryParameters.method
        if (!method.hasJavaResolutionFacade()) return

        val operatorSearcher = OperatorReferenceSearcher.create(
            queryParameters.method,
            queryParameters.effectiveSearchScope,
            consumer,
            queryParameters.optimizer,
            KotlinReferencesSearchOptions(acceptCallableOverrides = true)
        )
        operatorSearcher?.run()
    }
}
