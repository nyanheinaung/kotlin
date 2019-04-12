/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.codeInsight.intention.LowPriorityAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.text.StringUtil
import org.jetbrains.kotlin.idea.core.replaced
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset

class ToOrdinaryStringLiteralIntention : SelfTargetingOffsetIndependentIntention<KtStringTemplateExpression>(
    KtStringTemplateExpression::class.java,
    "To ordinary string literal"
), LowPriorityAction {
    override fun isApplicableTo(element: KtStringTemplateExpression): Boolean {
        return element.text.startsWith("\"\"\"")
    }

    override fun applyTo(element: KtStringTemplateExpression, editor: Editor?) {
        val startOffset = element.startOffset
        val endOffset = element.endOffset
        val currentOffset = editor?.caretModel?.currentCaret?.offset ?: startOffset

        val text = buildString {
            append("\"")

            for (entry in element.entries) {
                if (entry is KtLiteralStringTemplateEntry) {
                    var text = entry.text
                    text = text.replace("\\", "\\\\")
                    text = text.replace("\"", "\\\"")
                    text = StringUtil.convertLineSeparators(text, "\\n")
                    append(text)
                } else {
                    append(entry.text)
                }
            }

            append("\"")
        }
        val replaced = element.replaced(KtPsiFactory(element).createExpression(text))

        val offset = when {
            currentOffset - startOffset < 2 -> startOffset
            endOffset - currentOffset < 2 -> replaced.endOffset
            else -> maxOf(currentOffset - 2, replaced.startOffset)
        }
        editor?.caretModel?.moveToOffset(offset)
    }
}