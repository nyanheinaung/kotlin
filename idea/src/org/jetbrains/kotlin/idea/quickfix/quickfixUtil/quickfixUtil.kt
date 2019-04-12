/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.quickfix.quickfixUtil

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.idea.quickfix.KotlinQuickFixAction
import org.jetbrains.kotlin.idea.quickfix.KotlinSingleIntentionActionFactory
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType

inline fun <reified T : PsiElement> Diagnostic.createIntentionForFirstParentOfType(
    factory: (T) -> KotlinQuickFixAction<T>?
) = psiElement.getNonStrictParentOfType<T>()?.let(factory)


fun createIntentionFactory(
    factory: (Diagnostic) -> IntentionAction?
) = object : KotlinSingleIntentionActionFactory() {
    override fun createAction(diagnostic: Diagnostic) = factory(diagnostic)
}

fun KtPrimaryConstructor.addConstructorKeyword(): PsiElement? {
    val keyword = KtPsiFactory(this).createConstructorKeyword()
    return addAfter(keyword, modifierList ?: return null)
}
