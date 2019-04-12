/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.refactoring.introduce.extractClass

import com.intellij.psi.PsiElement
import com.intellij.refactoring.RefactoringBundle
import org.jetbrains.kotlin.idea.refactoring.introduce.extractClass.ui.KotlinExtractSuperclassDialog
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject

object KotlinExtractSuperclassHandler : KotlinExtractSuperHandlerBase(false) {
    val REFACTORING_NAME = "Extract Superclass"

    override fun getErrorMessage(klass: KtClassOrObject): String? {
        val superMessage = super.getErrorMessage(klass)
        if (superMessage != null) return superMessage
        if (klass is KtClass) {
            if (klass.isInterface()) return RefactoringBundle.message("superclass.cannot.be.extracted.from.an.interface")
            if (klass.isEnum()) return RefactoringBundle.message("superclass.cannot.be.extracted.from.an.enum")
            if (klass.isAnnotation()) return "Superclass cannot be extracted from an annotation class"
        }
        return null
    }

    override fun createDialog(klass: KtClassOrObject, targetParent: PsiElement) =
        KotlinExtractSuperclassDialog(
                originalClass = klass,
                targetParent = targetParent,
                conflictChecker = { checkConflicts(klass, it) },
                refactoring = { ExtractSuperRefactoring(it).performRefactoring() }
        )
}