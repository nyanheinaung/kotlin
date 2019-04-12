/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.idea.caches.resolve.resolveToCall
import org.jetbrains.kotlin.idea.project.languageVersionSettings
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.calls.model.ArgumentMatch

class AddNamesToCallArgumentsIntention : SelfTargetingRangeIntention<KtCallElement>(
    KtCallElement::class.java,
    "Add names to call arguments"
) {
    override fun applicabilityRange(element: KtCallElement): TextRange? {
        val arguments = element.valueArguments
        if (arguments.all { it.isNamed() || it is LambdaArgument }) return null

        val resolvedCall = element.resolveToCall() ?: return null
        if (!resolvedCall.resultingDescriptor.hasStableParameterNames()) return null

        if (arguments.all {
                AddNameToArgumentIntention.argumentMatchedAndCouldBeNamedInCall(it, resolvedCall, element.languageVersionSettings)
            }
        ) {
            return element.calleeExpression?.textRange
        }

        return null
    }

    override fun applyTo(element: KtCallElement, editor: Editor?) {
        val arguments = element.valueArguments
        val resolvedCall = element.resolveToCall() ?: return
        for (argument in arguments) {
            if (argument !is KtValueArgument || argument is KtLambdaArgument) continue
            val argumentMatch = resolvedCall.getArgumentMapping(argument) as? ArgumentMatch ?: continue
            val name = argumentMatch.valueParameter.name
            val newArgument = KtPsiFactory(element).createArgument(
                argument.getArgumentExpression()!!,
                name,
                argument.getSpreadElement() != null
            )
            argument.replace(newArgument)
        }
    }
}