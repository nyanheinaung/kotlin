/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.diagnostics

import com.intellij.openapi.util.Condition
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.Diagnostic
import java.util.ArrayList

class SimpleDiagnostics(diagnostics: Collection<Diagnostic>) : Diagnostics {
    //copy to prevent external change
    private val diagnostics = ArrayList(diagnostics)

    @Suppress("UNCHECKED_CAST")
    private val elementsCache = DiagnosticsElementsCache(this, { true })

    override fun all() = diagnostics

    override fun forElement(psiElement: PsiElement) = elementsCache.getDiagnostics(psiElement)

    override fun noSuppression() = this
}
