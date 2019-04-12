/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.quickfix.replaceWith

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.idea.codeInliner.UsageReplacementStrategy
import org.jetbrains.kotlin.idea.core.moveCaret
import org.jetbrains.kotlin.idea.core.targetDescriptors
import org.jetbrains.kotlin.idea.quickfix.CleanupFix
import org.jetbrains.kotlin.idea.quickfix.KotlinSingleIntentionActionFactory
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getCalleeExpressionIfAny

class DeprecatedSymbolUsageFix(
    element: KtSimpleNameExpression,
    replaceWith: ReplaceWith
) : DeprecatedSymbolUsageFixBase(element, replaceWith), CleanupFix, HighPriorityAction {

    override fun getFamilyName() = "Replace deprecated symbol usage"

    override fun getText() = "Replace with '${replaceWith.pattern}'"

    override fun invoke(replacementStrategy: UsageReplacementStrategy, project: Project, editor: Editor?) {
        val element = element ?: return
        val result = replacementStrategy.createReplacer(element)?.invoke()
        if (result != null) {
            val offset = (result.getCalleeExpressionIfAny() ?: result).textOffset
            editor?.moveCaret(offset)
        }
    }

    companion object : KotlinSingleIntentionActionFactory() {
        override fun createAction(diagnostic: Diagnostic): IntentionAction? {
            val (nameExpression, replacement) = extractDataFromDiagnostic(diagnostic) ?: return null
            return DeprecatedSymbolUsageFix(nameExpression, replacement)
        }

        fun isImportToBeRemoved(import: KtImportDirective): Boolean {
            if (import.isAllUnder) return false

            val targetDescriptors = import.targetDescriptors()
            if (targetDescriptors.isEmpty()) return false

            return targetDescriptors.all {
                fetchReplaceWithPattern(it, import.project, null) != null
            }
        }
    }
}
