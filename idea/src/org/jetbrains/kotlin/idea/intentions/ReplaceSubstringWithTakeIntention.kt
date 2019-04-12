/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

class ReplaceSubstringWithTakeIntention : ReplaceSubstringIntention("Replace 'substring' call with 'take' call") {
    override fun applicabilityRangeInner(element: KtDotQualifiedExpression): TextRange? {
        val arguments = element.callExpression?.valueArguments ?: return null
        if (arguments.size == 2 && element.isFirstArgumentZero()) {
            return getTextRange(element)
        }
        return null
    }

    override fun applyTo(element: KtDotQualifiedExpression, editor: Editor?) {
        val argument = element.callExpression!!.valueArguments[1].getArgumentExpression()!!
        element.replaceWith("$0.take($1)", argument)
    }
}
