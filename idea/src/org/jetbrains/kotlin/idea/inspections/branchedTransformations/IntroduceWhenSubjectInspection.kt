/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.inspections.branchedTransformations

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.inspections.AbstractApplicabilityBasedInspection
import org.jetbrains.kotlin.idea.intentions.branchedTransformations.getSubjectToIntroduce
import org.jetbrains.kotlin.idea.intentions.branchedTransformations.introduceSubject
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

class IntroduceWhenSubjectInspection : AbstractApplicabilityBasedInspection<KtWhenExpression>(KtWhenExpression::class.java) {

    override fun isApplicable(element: KtWhenExpression) = element.getSubjectToIntroduce() != null

    override fun inspectionTarget(element: KtWhenExpression) = element.whenKeyword

    override fun inspectionText(element: KtWhenExpression) = "'when' with subject should be used"

    override val defaultFixText = "Introduce 'when' subject"

    override fun fixText(element: KtWhenExpression): String {
        val subject = element.getSubjectToIntroduce() ?: return ""
        return "Introduce '${subject.text}' as subject of 'when'"
    }

    override fun applyTo(element: PsiElement, project: Project, editor: Editor?) {
        element.getParentOfType<KtWhenExpression>(strict = true)?.introduceSubject()
    }
}
