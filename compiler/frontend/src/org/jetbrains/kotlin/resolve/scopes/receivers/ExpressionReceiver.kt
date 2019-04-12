/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.scopes.receivers

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType

interface ExpressionReceiver : ReceiverValue {
    val expression: KtExpression

    companion object {
        private open class ExpressionReceiverImpl(
            override val expression: KtExpression, type: KotlinType, original: ReceiverValue?
        ) : AbstractReceiverValue(type, original), ExpressionReceiver {
            override fun replaceType(newType: KotlinType) = ExpressionReceiverImpl(expression, newType, original)

            override fun toString() = "$type {$expression: ${expression.text}}"
        }

        private class ThisExpressionClassReceiver(
            override val classDescriptor: ClassDescriptor,
            expression: KtExpression,
            type: KotlinType,
            original: ReceiverValue?
        ) : ExpressionReceiverImpl(expression, type, original), ThisClassReceiver {
            override fun replaceType(newType: KotlinType) = ThisExpressionClassReceiver(classDescriptor, expression, newType, original)
        }

        private class SuperExpressionReceiver(
            override val thisType: KotlinType,
            expression: KtExpression,
            type: KotlinType,
            original: ReceiverValue?
        ) : ExpressionReceiverImpl(expression, type, original), SuperCallReceiverValue {
            override fun replaceType(newType: KotlinType) = SuperExpressionReceiver(thisType, expression, newType, original)
        }

        fun create(
            expression: KtExpression,
            type: KotlinType,
            bindingContext: BindingContext
        ): ExpressionReceiver {
            var referenceExpression: KtReferenceExpression? = null
            if (expression is KtThisExpression) {
                referenceExpression = expression.instanceReference
            } else if (expression is KtConstructorDelegationReferenceExpression) { // todo check this
                referenceExpression = expression
            }

            if (referenceExpression != null) {
                val descriptor = bindingContext.get(BindingContext.REFERENCE_TARGET, referenceExpression)
                if (descriptor is ClassDescriptor) {
                    return ThisExpressionClassReceiver(descriptor.original, expression, type, original = null)
                }
            } else if (expression is KtSuperExpression) {
                // if there is no THIS_TYPE_FOR_SUPER_EXPRESSION in binding context, we fall through into more restrictive option
                // i.e. just return common ExpressionReceiverImpl
                bindingContext[BindingContext.THIS_TYPE_FOR_SUPER_EXPRESSION, expression]?.let { thisType ->
                    return SuperExpressionReceiver(thisType, expression, type, original = null)
                }
            }

            return ExpressionReceiverImpl(expression, type, original = null)
        }
    }
}
