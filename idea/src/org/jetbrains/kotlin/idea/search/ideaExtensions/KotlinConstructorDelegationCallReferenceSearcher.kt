/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.search.ideaExtensions

import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.psi.PsiReference
import com.intellij.psi.search.searches.MethodReferencesSearch.SearchParameters
import org.jetbrains.kotlin.compatibility.ExecutorProcessor
import org.jetbrains.kotlin.idea.caches.resolve.util.hasJavaResolutionFacade
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.idea.search.usagesSearch.processDelegationCallConstructorUsages

class KotlinConstructorDelegationCallReferenceSearcher : QueryExecutorBase<PsiReference, SearchParameters>(true) {
    override fun processQuery(queryParameters: SearchParameters, consumer: ExecutorProcessor<PsiReference>) {
        val method = queryParameters.method
        if (!method.isConstructor) return
        if (!method.hasJavaResolutionFacade()) return

        method.processDelegationCallConstructorUsages(method.useScope.intersectWith(queryParameters.effectiveSearchScope)) {
            it.calleeExpression?.mainReference?.let { consumer.process(it) } ?: true
        }
    }
}