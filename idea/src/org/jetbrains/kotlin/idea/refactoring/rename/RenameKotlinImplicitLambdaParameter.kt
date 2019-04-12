/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.refactoring.rename

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.intentions.ReplaceItWithExplicitFunctionLiteralParamIntention
import org.jetbrains.kotlin.idea.intentions.isAutoCreatedItUsage
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

class RenameKotlinImplicitLambdaParameter : KotlinVariableInplaceRenameHandler() {
    override fun isAvailable(element: PsiElement?, editor: Editor, file: PsiFile): Boolean {
        val nameExpression = file.findElementForRename<KtNameReferenceExpression>(editor.caretModel.offset)

        return nameExpression != null && isAutoCreatedItUsage(nameExpression)
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?, dataContext: DataContext) {
        val intention = ReplaceItWithExplicitFunctionLiteralParamIntention()
        project.executeWriteCommand("Convert 'it' to explicit lambda parameter") {
            if (file != null) intention.invoke(project, editor, file)
        }
    }

    override fun invoke(project: Project, elements: Array<out PsiElement>, dataContext: DataContext) {
        // Do nothing: this method is called not from editor
    }
}
