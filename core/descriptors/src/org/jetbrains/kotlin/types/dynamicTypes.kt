/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import org.jetbrains.kotlin.renderer.DescriptorRendererOptions
import org.jetbrains.kotlin.types.model.DynamicTypeMarker
import org.jetbrains.kotlin.types.typeUtil.builtIns

open class DynamicTypesSettings {
    open val dynamicTypesAllowed: Boolean
        get() = false
}

class DynamicTypesAllowed: DynamicTypesSettings() {
    override val dynamicTypesAllowed: Boolean
        get() = true
}

fun KotlinType.isDynamic(): Boolean = unwrap() is DynamicType

fun createDynamicType(builtIns: KotlinBuiltIns) = DynamicType(builtIns, Annotations.EMPTY)

class DynamicType(
    builtIns: KotlinBuiltIns,
    override val annotations: Annotations
) : FlexibleType(builtIns.nothingType, builtIns.nullableAnyType), DynamicTypeMarker {
    override val delegate: SimpleType get() = upperBound

    // Nullability has no effect on dynamics
    override fun makeNullableAsSpecified(newNullability: Boolean): DynamicType = this

    override val isMarkedNullable: Boolean get() = false

    override fun replaceAnnotations(newAnnotations: Annotations): DynamicType = DynamicType(delegate.builtIns, newAnnotations)

    override fun render(renderer: DescriptorRenderer, options: DescriptorRendererOptions): String = "dynamic"
}
