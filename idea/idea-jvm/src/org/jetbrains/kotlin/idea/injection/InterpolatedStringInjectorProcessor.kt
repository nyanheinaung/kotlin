/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.injection

import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.Trinity
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import org.intellij.plugins.intelliLang.inject.InjectedLanguage
import org.intellij.plugins.intelliLang.inject.InjectorUtils
import org.intellij.plugins.intelliLang.inject.config.BaseInjection
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.constants.evaluate.ConstantExpressionEvaluator
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

typealias Injection = Trinity<PsiLanguageInjectionHost, InjectedLanguage, TextRange>

data class InjectionSplitResult(val isUnparsable: Boolean, val ranges: List<Injection>)

fun splitLiteralToInjectionParts(injection: BaseInjection, literal: KtStringTemplateExpression): InjectionSplitResult? {
    InjectorUtils.getLanguage(injection) ?: return null

    fun injectionRange(range: TextRange, prefix: String, suffix: String): Injection {
        TextRange.assertProperRange(range, injection)
        val injectedLanguage = InjectedLanguage.create(injection.injectedLanguageId, prefix, suffix, true)!!
        return Trinity.create(literal, injectedLanguage, range)
    }

    tailrec fun collectInjections(
        children: List<PsiElement>,
        pendingPrefix: String,
        unparseable: Boolean,
        collected: MutableList<Injection>
    ): InjectionSplitResult {
        val child = children.firstOrNull() ?: return InjectionSplitResult(unparseable, collected)
        val tail = children.subList(1, children.size)
        val partOffsetInParent = child.startOffsetInParent

        if (child is KtLiteralStringTemplateEntry || child is KtEscapeStringTemplateEntry) {
            val consequentStringsCount = tail.asSequence()
                .takeWhile { it is KtLiteralStringTemplateEntry || it is KtEscapeStringTemplateEntry }
                .count()

            val lastChild = children[consequentStringsCount]
            val remaining = tail.subList(consequentStringsCount, tail.size)

            collected += injectionRange(
                TextRange.create(partOffsetInParent, lastChild.startOffsetInParent + lastChild.textLength),
                pendingPrefix,
                if (remaining.isEmpty()) injection.suffix else ""
            )
            return collectInjections(remaining, "", unparseable, collected)
        } else {
            if (pendingPrefix.isNotEmpty() || collected.isEmpty()) {
                // Store pending prefix before creating a new one,
                // or if it is a first part then create a dummy injection to distinguish "inner" prefixes
                collected += injectionRange(TextRange.from(partOffsetInParent, 0), pendingPrefix, "")
            }

            val (prefix, myUnparseable) = makePlaceholder(child)

            if (tail.isEmpty()) {
                // There won't be more elements, so create part with prefix right away
                collected += injectionRange(TextRange.from(partOffsetInParent + child.textLength, 0), prefix, injection.suffix)
            }
            return collectInjections(tail, prefix, unparseable || myUnparseable, collected)
        }
    }

    return collectInjections(literal.children.toList(), injection.prefix, false, ArrayList())
}

private fun makePlaceholder(child: PsiElement): Pair<String, Boolean> = when (child) {
    is KtSimpleNameStringTemplateEntry ->
        tryEvaluateConstant(child.expression)?.let { it to false } ?: ((child.expression?.text ?: NO_VALUE_NAME) to true)
    is KtBlockStringTemplateEntry ->
        tryEvaluateConstant(child.expression)?.let { it to false } ?: (NO_VALUE_NAME to true)
    else ->
        ((child.text ?: NO_VALUE_NAME) to true)
}

private fun tryEvaluateConstant(ktExpression: KtExpression?) =
    ktExpression?.let { expression ->
        ConstantExpressionEvaluator.getConstant(expression, expression.analyze())
            ?.takeUnless { it.isError }
            ?.getValue(TypeUtils.NO_EXPECTED_TYPE)
            ?.safeAs<String>()
    }

private const val NO_VALUE_NAME = "missingValue"
