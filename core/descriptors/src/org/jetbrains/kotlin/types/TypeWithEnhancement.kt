/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types

import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import org.jetbrains.kotlin.renderer.DescriptorRendererOptions

interface TypeWithEnhancement {
    val origin: UnwrappedType
    val enhancement: KotlinType
}

class SimpleTypeWithEnhancement(
        override val delegate: SimpleType,
        override val enhancement: KotlinType
) : DelegatingSimpleType(),
    TypeWithEnhancement {

    override val origin: UnwrappedType get() = delegate

    override fun replaceAnnotations(newAnnotations: Annotations): SimpleType
            = origin.replaceAnnotations(newAnnotations).wrapEnhancement(enhancement) as SimpleType

    override fun makeNullableAsSpecified(newNullability: Boolean): SimpleType
            = origin.makeNullableAsSpecified(newNullability).wrapEnhancement(enhancement.unwrap().makeNullableAsSpecified(newNullability)) as SimpleType
}

class FlexibleTypeWithEnhancement(
        override val origin: FlexibleType,
        override val enhancement: KotlinType
) : FlexibleType(origin.lowerBound, origin.upperBound),
    TypeWithEnhancement {

    override fun replaceAnnotations(newAnnotations: Annotations): UnwrappedType
            = origin.replaceAnnotations(newAnnotations).wrapEnhancement(enhancement)

    override fun makeNullableAsSpecified(newNullability: Boolean): UnwrappedType
            = origin.makeNullableAsSpecified(newNullability).wrapEnhancement(enhancement.unwrap().makeNullableAsSpecified(newNullability))

    override fun render(renderer: DescriptorRenderer, options: DescriptorRendererOptions): String {
        if (options.enhancedTypes) {
            return renderer.renderType(enhancement)
        }
        return origin.render(renderer, options)
    }

    override val delegate: SimpleType get() = origin.delegate
}

fun KotlinType.getEnhancement(): KotlinType? = when (this) {
    is TypeWithEnhancement -> enhancement
    else -> null
}

fun KotlinType.unwrapEnhancement(): KotlinType = getEnhancement() ?: this

fun UnwrappedType.inheritEnhancement(origin: KotlinType): UnwrappedType = wrapEnhancement(origin.getEnhancement())

fun UnwrappedType.wrapEnhancement(enhancement: KotlinType?): UnwrappedType {
    if (enhancement == null) {
        return this
    }

    return when (this) {
        is SimpleType -> SimpleTypeWithEnhancement(this, enhancement)
        is FlexibleType -> FlexibleTypeWithEnhancement(this, enhancement)
    }
}
