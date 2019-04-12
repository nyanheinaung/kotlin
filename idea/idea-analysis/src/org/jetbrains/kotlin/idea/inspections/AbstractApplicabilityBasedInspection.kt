/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.inspections

import com.intellij.codeInspection.*
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtVisitorVoid

abstract class AbstractApplicabilityBasedInspection<TElement: KtElement>(
        val elementType: Class<TElement>
) : AbstractKotlinInspection() {

    final override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession) =
            object : KtVisitorVoid() {
                override fun visitKtElement(element: KtElement) {
                    super.visitKtElement(element)

                    if (!elementType.isInstance(element) || element.textLength == 0) return
                    visitTargetElement(element as TElement, holder, isOnTheFly)
                }
            }

    // This function should be called from visitor built by a derived inspection
    protected fun visitTargetElement(element: TElement, holder: ProblemsHolder, isOnTheFly: Boolean) {
        if (!isApplicable(element)) return

        holder.registerProblemWithoutOfflineInformation(
                inspectionTarget(element),
                inspectionText(element),
                isOnTheFly,
                inspectionHighlightType(element),
                LocalFix(fixText(element))
        )
    }

    open fun inspectionTarget(element: TElement): PsiElement = element

    open fun inspectionHighlightType(element: TElement): ProblemHighlightType = ProblemHighlightType.GENERIC_ERROR_OR_WARNING

    abstract fun inspectionText(element: TElement): String

    abstract val defaultFixText: String

    open fun fixText(element: TElement) = defaultFixText

    abstract fun isApplicable(element: TElement): Boolean

    abstract fun applyTo(element: PsiElement, project: Project = element.project, editor: Editor? = null)

    open val startFixInWriteAction = true

    private inner class LocalFix(val text: String) : LocalQuickFix {
        override fun startInWriteAction() = startFixInWriteAction

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val element = descriptor.psiElement
            applyTo(element, project, element.findExistingEditor())
        }

        override fun getFamilyName() = defaultFixText

        override fun getName() = text
    }
}