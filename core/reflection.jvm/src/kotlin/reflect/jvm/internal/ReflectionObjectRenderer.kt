/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.Variance
import kotlin.reflect.KParameter

internal object ReflectionObjectRenderer {
    private val renderer = DescriptorRenderer.FQ_NAMES_IN_TYPES

    private fun StringBuilder.appendReceiverType(receiver: ReceiverParameterDescriptor?) {
        if (receiver != null) {
            append(renderType(receiver.type))
            append(".")
        }
    }

    private fun StringBuilder.appendReceivers(callable: CallableDescriptor) {
        val dispatchReceiver = callable.instanceReceiverParameter
        val extensionReceiver = callable.extensionReceiverParameter

        appendReceiverType(dispatchReceiver)

        val addParentheses = dispatchReceiver != null && extensionReceiver != null
        if (addParentheses) append("(")
        appendReceiverType(extensionReceiver)
        if (addParentheses) append(")")
    }

    private fun renderCallable(descriptor: CallableDescriptor): String {
        return when (descriptor) {
            is PropertyDescriptor -> renderProperty(descriptor)
            is FunctionDescriptor -> renderFunction(descriptor)
            else -> error("Illegal callable: $descriptor")
        }
    }

    // TODO: include visibility
    fun renderProperty(descriptor: PropertyDescriptor): String {
        return buildString {
            append(if (descriptor.isVar) "var " else "val ")
            appendReceivers(descriptor)
            append(renderer.renderName(descriptor.name, true))

            append(": ")
            append(renderType(descriptor.type))
        }
    }

    fun renderFunction(descriptor: FunctionDescriptor): String {
        return buildString {
            append("fun ")
            appendReceivers(descriptor)
            append(renderer.renderName(descriptor.name, true))

            descriptor.valueParameters.joinTo(this, separator = ", ", prefix = "(", postfix = ")") {
                renderType(it.type) // TODO: vararg
            }

            append(": ")
            append(renderType(descriptor.returnType!!))
        }
    }

    fun renderLambda(invoke: FunctionDescriptor): String {
        return buildString {
            appendReceivers(invoke)

            invoke.valueParameters.joinTo(this, separator = ", ", prefix = "(", postfix = ")") {
                renderType(it.type)
            }

            append(" -> ")
            append(renderType(invoke.returnType!!))
        }
    }

    fun renderParameter(parameter: KParameterImpl): String {
        return buildString {
            when (parameter.kind) {
                KParameter.Kind.EXTENSION_RECEIVER -> append("extension receiver")
                KParameter.Kind.INSTANCE -> append("instance")
                KParameter.Kind.VALUE -> append("parameter #${parameter.index} ${parameter.name}")
            }

            append(" of ")
            append(renderCallable(parameter.callable.descriptor))
        }
    }

    fun renderTypeParameter(typeParameter: TypeParameterDescriptor): String {
        return buildString {
            when (typeParameter.variance) {
                Variance.INVARIANT -> {
                }
                Variance.IN_VARIANCE -> append("in ")
                Variance.OUT_VARIANCE -> append("out ")
            }

            append(typeParameter.name)
        }
    }

    fun renderType(type: KotlinType): String {
        return renderer.renderType(type)
    }
}
