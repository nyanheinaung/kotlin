/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.liveTemplates.macro

import com.intellij.codeInsight.template.JavaPsiElementResult
import com.intellij.psi.PsiNamedElement

class KotlinPsiElementResult(element: PsiNamedElement) : JavaPsiElementResult(element) {
    override fun toString() = (element as PsiNamedElement).name ?: ""
}
