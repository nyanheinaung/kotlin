/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.highlighter

import org.jetbrains.kotlin.idea.inspections.UnusedSymbolInspection
import org.jetbrains.kotlin.idea.isMainFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter

class KotlinPsiCheckerAndHighlightingUpdater : KotlinPsiChecker() {
    override fun shouldSuppressUnusedParameter(parameter: KtParameter): Boolean {
        val grandParent = parameter.parent.parent as? KtNamedFunction ?: return false
        if (!UnusedSymbolInspection.isEntryPoint(grandParent)) return false
        return !grandParent.isMainFunction()
    }
}
