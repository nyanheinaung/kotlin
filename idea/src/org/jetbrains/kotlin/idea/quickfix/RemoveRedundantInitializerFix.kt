/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.quickfix

import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.psi.KtProperty

class RemoveRedundantInitializerFix(element: KtProperty) : RemovePartsFromPropertyFix(element, true, false, false) {

    override fun getText(): String = "Remove redundant initializer"

    override fun getFamilyName(): String = "Remove redundant initializer"

    companion object : KotlinSingleIntentionActionFactory() {
        override fun createAction(diagnostic: Diagnostic): KotlinQuickFixAction<KtProperty>? {
            val element = diagnostic.psiElement
            val property = PsiTreeUtil.getParentOfType(element, KtProperty::class.java) ?: return null
            return RemoveRedundantInitializerFix(property)
        }
    }

}