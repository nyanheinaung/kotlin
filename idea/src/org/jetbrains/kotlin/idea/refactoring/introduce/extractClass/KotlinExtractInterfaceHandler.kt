/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.refactoring.introduce.extractClass

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.refactoring.introduce.extractClass.ui.KotlinExtractInterfaceDialog
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject

object KotlinExtractInterfaceHandler : KotlinExtractSuperHandlerBase(true) {
    val REFACTORING_NAME = "Extract Interface"

    override fun getErrorMessage(klass: KtClassOrObject): String? {
        val superMessage = super.getErrorMessage(klass)
        if (superMessage != null) return superMessage
        if (klass is KtClass && klass.isAnnotation()) return "Interface cannot be extracted from an annotation class"
        return null
    }

    override fun createDialog(klass: KtClassOrObject, targetParent: PsiElement) =
        KotlinExtractInterfaceDialog(
                originalClass = klass,
                targetParent = targetParent,
                conflictChecker = { checkConflicts(klass, it) },
                refactoring = { ExtractSuperRefactoring(it).performRefactoring() }
        )
}