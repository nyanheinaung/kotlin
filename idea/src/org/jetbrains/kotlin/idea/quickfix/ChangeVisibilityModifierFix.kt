/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.quickfix

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

class ChangeVisibilityModifierFix(element: KtDeclaration) : KotlinQuickFixAction<KtDeclaration>(element) {
    override fun getFamilyName() = "Use inherited visibility"

    override fun getText() = familyName

    public override fun invoke(project: Project, editor: Editor?, file: KtFile) {
        val element = element ?: return
        element.removeModifier(element.visibilityModifierType()!!)
    }

    companion object : KotlinSingleIntentionActionFactory() {
        override fun createAction(diagnostic: Diagnostic): KotlinQuickFixAction<KtDeclaration>? {
            val element = diagnostic.psiElement as? KtDeclaration ?: return null
            return ChangeVisibilityModifierFix(element)
        }
    }
}
