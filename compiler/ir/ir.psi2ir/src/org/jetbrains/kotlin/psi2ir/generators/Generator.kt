/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.generators

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.ir.builders.IrGenerator
import org.jetbrains.kotlin.ir.builders.IrGeneratorWithScope
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.util.slicedMap.ReadOnlySlice
import java.lang.RuntimeException


interface Generator : IrGenerator {
    override val context: GeneratorContext
}

interface GeneratorWithScope : Generator, IrGeneratorWithScope


fun <K, V : Any> Generator.get(slice: ReadOnlySlice<K, V>, key: K): V? =
    context.bindingContext[slice, key]

fun <K, V : Any> Generator.getOrFail(slice: ReadOnlySlice<K, V>, key: K): V =
    context.bindingContext[slice, key] ?: throw RuntimeException("No $slice for $key")

inline fun <K, V : Any> Generator.getOrFail(slice: ReadOnlySlice<K, V>, key: K, message: (K) -> String): V =
    context.bindingContext[slice, key] ?: throw RuntimeException(message(key))

fun Generator.getInferredTypeWithImplicitCasts(key: KtExpression): KotlinType? =
    context.bindingContext.getType(key)

fun Generator.getInferredTypeWithImplicitCastsOrFail(key: KtExpression): KotlinType =
    getInferredTypeWithImplicitCasts(key) ?: throw RuntimeException("No type for expression: ${key.text}")

fun Generator.getResolvedCall(key: KtElement): ResolvedCall<out CallableDescriptor>? =
    key.getResolvedCall(context.bindingContext)

