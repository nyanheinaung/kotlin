/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.diagnostics

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.Diagnostic
import com.intellij.openapi.util.ModificationTracker

interface Diagnostics : Iterable<Diagnostic> {
    //should not be called on readonly views
    //any Diagnostics object returned by BindingContext#getDiagnostics() should implement this property
    val modificationTracker: ModificationTracker
        get() = throw IllegalStateException("Trying to obtain modification tracker for Diagnostics object of class ${this::class.java}")

    fun all(): Collection<Diagnostic>

    fun forElement(psiElement: PsiElement): Collection<Diagnostic>

    fun isEmpty(): Boolean = all().isEmpty()

    fun noSuppression(): Diagnostics

    override fun iterator() = all().iterator()

    companion object {
        val EMPTY: Diagnostics = object : Diagnostics {
            override fun noSuppression(): Diagnostics = this
            override val modificationTracker: ModificationTracker = ModificationTracker.NEVER_CHANGED
            override fun all() = listOf<Diagnostic>()
            override fun forElement(psiElement: PsiElement) = listOf<Diagnostic>()
        }
    }
}
