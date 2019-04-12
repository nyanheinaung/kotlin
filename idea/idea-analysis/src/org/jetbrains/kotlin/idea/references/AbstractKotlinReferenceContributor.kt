/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.references

import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import org.jetbrains.kotlin.psi.KtElement

abstract class AbstractKotlinReferenceContributor : PsiReferenceContributor() {
    protected inline fun <reified E : KtElement> PsiReferenceRegistrar.registerProvider(
            priority: Double = PsiReferenceRegistrar.DEFAULT_PRIORITY,
            crossinline factory: (E) -> PsiReference?
    ) {
        registerMultiProvider<E>(priority) { factory(it)?.let { arrayOf(it) } ?: PsiReference.EMPTY_ARRAY }
    }

    protected inline fun <reified E : KtElement> PsiReferenceRegistrar.registerMultiProvider(
            priority: Double = PsiReferenceRegistrar.DEFAULT_PRIORITY,
            crossinline factory: (E) -> Array<PsiReference>
    ) {
        registerMultiProvider(PlatformPatterns.psiElement(E::class.java), priority, factory)
    }

    protected inline fun <E : KtElement> PsiReferenceRegistrar.registerMultiProvider(
            pattern: ElementPattern<E>,
            priority: Double = PsiReferenceRegistrar.DEFAULT_PRIORITY,
            crossinline factory: (E) -> Array<PsiReference>
    ) {
        registerReferenceProvider(
                pattern,
                object : PsiReferenceProvider() {
                    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
                        @Suppress("UNCHECKED_CAST")
                        return factory(element as E)
                    }
                },
                priority
        )
    }
}