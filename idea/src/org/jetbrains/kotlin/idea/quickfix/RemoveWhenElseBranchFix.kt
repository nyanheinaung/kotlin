/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.quickfix

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtWhenEntry

class RemoveWhenElseBranchFix(element: KtWhenEntry) : KotlinQuickFixAction<KtWhenEntry>(element) {
    override fun getFamilyName() = "Remove else branch"

    override fun getText() = familyName

    override fun invoke(project: Project, editor: Editor?, file: KtFile) {
        element?.delete()
    }

    companion object : KotlinSingleIntentionActionFactory() {
        override fun createAction(diagnostic: Diagnostic): RemoveWhenElseBranchFix? {
            return (diagnostic.psiElement as? KtWhenEntry)?.let { RemoveWhenElseBranchFix(it) }
        }
    }
}
