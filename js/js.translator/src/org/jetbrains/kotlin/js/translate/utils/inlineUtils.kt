/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:JvmName("InlineUtils")

package org.jetbrains.kotlin.js.translate.utils

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.js.backend.ast.*
import org.jetbrains.kotlin.js.backend.ast.metadata.*
import org.jetbrains.kotlin.js.inline.util.isCallInvocation
import org.jetbrains.kotlin.js.translate.context.TranslationContext
import org.jetbrains.kotlin.js.translate.reference.CallExpressionTranslator
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.inline.InlineStrategy

/**
 * Recursively walks expression and sets metadata for all invocations of descriptor.
 *
 * When JetExpression is compiled, the resulting JsExpression
 * might not be JsInvocation.
 *
 * For example, extension call with nullable receiver:
 *  x?.fn(y)
 * will compile to:
 *  (x != null) ? fn.call(x, y) : null
 */
fun setInlineCallMetadata(
    expression: JsExpression,
    psiElement: KtElement,
    descriptor: CallableDescriptor,
    context: TranslationContext
) {
    assert(CallExpressionTranslator.shouldBeInlined(descriptor)) {
        "Expected descriptor of callable, that should be inlined, but got: $descriptor"
    }

    val candidateNames = setOf(context.aliasedName(descriptor), context.getInnerNameForDescriptor(descriptor))

    val visitor = object : RecursiveJsVisitor() {
        override fun visitInvocation(invocation: JsInvocation) {
            super.visitInvocation(invocation)

            if (invocation.name in candidateNames || invocation.name?.descriptor?.original == descriptor.original) {
                invocation.descriptor = descriptor
                invocation.inlineStrategy = InlineStrategy.IN_PLACE
                invocation.psiElement = psiElement
            }
        }
    }

    visitor.accept(expression)

    context.addInlineCall(descriptor)
}

fun setInlineCallMetadata(
        expression: JsExpression,
        psiElement: KtElement,
        resolvedCall: ResolvedCall<*>,
        context: TranslationContext
) = setInlineCallMetadata(expression, psiElement, PsiUtils.getFunctionDescriptor(resolvedCall), context)

fun setInlineCallMetadata(
        nameRef: JsNameRef,
        psiElement: KtElement,
        descriptor: CallableDescriptor,
        context: TranslationContext
) {
    if (nameRef.inlineStrategy != null) return
    nameRef.descriptor = descriptor
    nameRef.inlineStrategy = InlineStrategy.IN_PLACE
    nameRef.psiElement = psiElement

    context.addInlineCall(descriptor)
}

fun TranslationContext.aliasedName(descriptor: CallableDescriptor): JsName {
    val alias = getAliasForDescriptor(descriptor)
    val aliasName = (alias as? JsNameRef)?.name

    return aliasName ?: getNameForDescriptor(descriptor)
}

val JsExpression?.name: JsName?
    get() = when (this) {
        is JsInvocation -> {
            val qualifier = this.qualifier

            when {
                isCallInvocation(this) -> (qualifier as JsNameRef).qualifier.name
                else -> qualifier.name
            }
        }
        is JsNameRef -> this.name
        else -> null
    }
