/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.quickfix

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.android.inspections.lint.AndroidLintQuickFix
import org.jetbrains.android.inspections.lint.AndroidQuickfixContexts
import org.jetbrains.android.util.AndroidBundle
import org.jetbrains.kotlin.android.canAddParcelable
import org.jetbrains.kotlin.android.implementParcelable
import org.jetbrains.kotlin.android.isParcelize
import org.jetbrains.kotlin.psi.KtClass


class ParcelableQuickFix : AndroidLintQuickFix {
    override fun apply(startElement: PsiElement, endElement: PsiElement, context: AndroidQuickfixContexts.Context) {
        startElement.getTargetClass()?.implementParcelable()
    }

    override fun isApplicable(startElement: PsiElement, endElement: PsiElement, contextType: AndroidQuickfixContexts.ContextType): Boolean {
        val targetClass = startElement.getTargetClass() ?: return false
        return targetClass.canAddParcelable() && !targetClass.isParcelize()
    }

    override fun getName(): String = AndroidBundle.message("implement.parcelable.intention.text")

    private fun PsiElement.getTargetClass(): KtClass? = PsiTreeUtil.getParentOfType(this, KtClass::class.java, false)
}