/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.codeInsight.intention.LowPriorityAction
import com.intellij.openapi.editor.Editor
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.core.moveInsideParentheses
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.psiUtil.containsInside

class MoveLambdaInsideParenthesesIntention : SelfTargetingIntention<KtLambdaArgument>(
    KtLambdaArgument::class.java, "Move lambda argument into parentheses"
), LowPriorityAction {
    override fun isApplicableTo(element: KtLambdaArgument, caretOffset: Int): Boolean {
        val body = element.getLambdaExpression()?.bodyExpression ?: return true
        return !body.textRange.containsInside(caretOffset)
    }

    override fun applyTo(element: KtLambdaArgument, editor: Editor?) {
        element.moveInsideParentheses(element.analyze())
    }
}

