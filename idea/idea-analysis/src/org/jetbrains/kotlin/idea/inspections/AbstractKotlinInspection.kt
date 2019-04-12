/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.inspections

import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.*
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.asJava.unwrapped
import org.jetbrains.kotlin.caches.resolve.KotlinCacheService
import org.jetbrains.kotlin.diagnostics.Severity
import org.jetbrains.kotlin.idea.highlighter.createSuppressWarningActions

abstract class AbstractKotlinInspection : LocalInspectionTool(), CustomSuppressableInspectionTool {
    override fun getSuppressActions(element: PsiElement?): Array<SuppressIntentionAction>? {
        if (element == null) return emptyArray()

        return createSuppressWarningActions(element, toSeverity(defaultLevel), suppressionKey).toTypedArray()
    }

    override fun isSuppressedFor(element: PsiElement): Boolean {
        if (SuppressManager.getInstance()!!.isSuppressedFor(element, id)) {
            return true
        }

        val project = element.project
        if (KotlinCacheService.getInstance(project).getSuppressionCache().isSuppressed(element, suppressionKey, toSeverity(defaultLevel))) {
            return true
        }

        return false
    }

    protected open val suppressionKey: String get() = this.shortName.removePrefix("Kotlin")

    protected fun ProblemsHolder.registerProblemWithoutOfflineInformation(
        element: PsiElement,
        description: String,
        isOnTheFly: Boolean,
        highlightType: ProblemHighlightType,
        vararg fixes: LocalQuickFix
    ) {
        registerProblemWithoutOfflineInformation(element, description, isOnTheFly, highlightType, null, *fixes)
    }

    protected fun ProblemsHolder.registerProblemWithoutOfflineInformation(
        element: PsiElement,
        description: String,
        isOnTheFly: Boolean,
        highlightType: ProblemHighlightType,
        range: TextRange?,
        vararg fixes: LocalQuickFix
    ) {
        if (!isOnTheFly && highlightType == ProblemHighlightType.INFORMATION) return
        val problemDescriptor = manager.createProblemDescriptor(element, range, description, highlightType, isOnTheFly, *fixes)
        registerProblem(problemDescriptor)
    }
}

fun toSeverity(highlightDisplayLevel: HighlightDisplayLevel): Severity {
    return when (highlightDisplayLevel) {
        HighlightDisplayLevel.DO_NOT_SHOW -> Severity.INFO

        HighlightDisplayLevel.WARNING,
        HighlightDisplayLevel.WEAK_WARNING -> Severity.WARNING

        HighlightDisplayLevel.ERROR,
        HighlightDisplayLevel.GENERIC_SERVER_ERROR_OR_WARNING,
        HighlightDisplayLevel.NON_SWITCHABLE_ERROR -> Severity.ERROR

        else -> Severity.ERROR
    }
}

@Suppress("unused")
fun Array<ProblemDescriptor>.registerWithElementsUnwrapped(
    holder: ProblemsHolder,
    isOnTheFly: Boolean,
    quickFixSubstitutor: ((LocalQuickFix, PsiElement) -> LocalQuickFix?)? = null
) {
    forEach { problem ->
        @Suppress("UNCHECKED_CAST")
        val originalFixes = problem.fixes as? Array<LocalQuickFix> ?: LocalQuickFix.EMPTY_ARRAY
        val newElement = problem.psiElement.unwrapped ?: return@forEach
        val newFixes = quickFixSubstitutor?.let { subst ->
            originalFixes.mapNotNull { subst(it, newElement) }.toTypedArray()
        } ?: originalFixes
        val descriptor =
            holder.manager.createProblemDescriptor(newElement, problem.descriptionTemplate, isOnTheFly, newFixes, problem.highlightType)
        holder.registerProblem(descriptor)
    }
}