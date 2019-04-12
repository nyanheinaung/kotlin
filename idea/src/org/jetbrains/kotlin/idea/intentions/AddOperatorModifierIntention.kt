/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.refactoring.withExpectedActuals
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.util.OperatorChecks

class AddOperatorModifierIntention : SelfTargetingRangeIntention<KtNamedFunction>(KtNamedFunction::class.java, "Add 'operator' modifier") {
    override fun applicabilityRange(element: KtNamedFunction): TextRange? {
        val nameIdentifier = element.nameIdentifier ?: return null
        val functionDescriptor = element.resolveToDescriptorIfAny() ?: return null
        if (functionDescriptor.isOperator || !OperatorChecks.check(functionDescriptor).isSuccess) return null
        return nameIdentifier.textRange
    }

    override fun applyTo(element: KtNamedFunction, editor: Editor?) {
        element.withExpectedActuals().forEach { it.addModifier(KtTokens.OPERATOR_KEYWORD) }
    }
}

