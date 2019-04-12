/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.quickfix

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory

class ChangeToUseSpreadOperatorFix(element: KtExpression) : KotlinQuickFixAction<KtExpression>(element) {
    override fun getFamilyName() = "Change to use spread operator"

    override fun getText() = "Change '${element?.text}' to '*${element?.text}'"

    override fun invoke(project: Project, editor: Editor?, file: KtFile) {
        val element = element ?: return
        val star = KtPsiFactory(project).createStar()
        element.parent.addBefore(star, element)
    }
}