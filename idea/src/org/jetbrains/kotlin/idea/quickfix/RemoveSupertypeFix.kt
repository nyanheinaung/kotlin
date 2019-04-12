/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.quickfix

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

class RemoveSupertypeFix(superClass: KtSuperTypeListEntry) : KotlinQuickFixAction<KtSuperTypeListEntry>(superClass) {
    override fun getFamilyName() = "Remove supertype"

    override fun getText() = familyName

    public override fun invoke(project: Project, editor: Editor?, file: KtFile) {
        val element = element ?: return
        element.getStrictParentOfType<KtClassOrObject>()?.removeSuperTypeListEntry(element)
    }

    companion object : KotlinSingleIntentionActionFactory() {
        override fun createAction(diagnostic: Diagnostic): KotlinQuickFixAction<KtSuperTypeListEntry>? {
            val superClass = diagnostic.psiElement.getNonStrictParentOfType<KtSuperTypeListEntry>() ?: return null
            return RemoveSupertypeFix(superClass)
        }
    }
}
