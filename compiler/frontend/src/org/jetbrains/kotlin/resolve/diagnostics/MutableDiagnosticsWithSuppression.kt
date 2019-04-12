/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.diagnostics

import org.jetbrains.kotlin.diagnostics.Diagnostic
import java.util.ArrayList
import com.intellij.openapi.util.CompositeModificationTracker
import com.intellij.util.CachedValueImpl
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.PsiElement
import org.jetbrains.annotations.TestOnly
import org.jetbrains.kotlin.resolve.BindingContext

class MutableDiagnosticsWithSuppression @JvmOverloads constructor(
    private val bindingContext: BindingContext,
    private val delegateDiagnostics: Diagnostics = Diagnostics.EMPTY
) : Diagnostics {
    private val diagnosticList = ArrayList<Diagnostic>()

    //NOTE: CachedValuesManager is not used because it requires Project passed to this object
    private val cache = CachedValueImpl(CachedValueProvider {
        val allDiagnostics = delegateDiagnostics.noSuppression().all() + diagnosticList
        CachedValueProvider.Result(DiagnosticsWithSuppression(bindingContext, allDiagnostics), modificationTracker)
    })

    private fun readonlyView(): DiagnosticsWithSuppression = cache.value!!

    override val modificationTracker = CompositeModificationTracker(delegateDiagnostics.modificationTracker)

    override fun all(): Collection<Diagnostic> = readonlyView().all()
    override fun forElement(psiElement: PsiElement) = readonlyView().forElement(psiElement)
    override fun noSuppression() = readonlyView().noSuppression()

    //essential that this list is readonly
    fun getOwnDiagnostics(): List<Diagnostic> {
        return diagnosticList
    }

    fun report(diagnostic: Diagnostic) {
        diagnosticList.add(diagnostic)
        modificationTracker.incModificationCount()
    }

    fun clear() {
        diagnosticList.clear()
        modificationTracker.incModificationCount()
    }

    @TestOnly
    fun getReadonlyView(): DiagnosticsWithSuppression = readonlyView()
}
