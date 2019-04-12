/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.refactoring.introduce

import com.intellij.refactoring.actions.*
import com.intellij.psi.*
import org.jetbrains.kotlin.psi.*

abstract class AbstractIntroduceAction : BasePlatformRefactoringAction() {
    init {
        setInjectedContext(true)
    }

    override final fun setInjectedContext(worksInInjected: Boolean) {
        super.setInjectedContext(worksInInjected)
    }

    override fun isAvailableInEditorOnly(): Boolean = true

    override fun isEnabledOnElements(elements: Array<out PsiElement>): Boolean =
            elements.all { it is KtElement }
}