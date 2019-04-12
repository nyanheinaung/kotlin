/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassifierDescriptorWithTypeParameters
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns

class StarProjectionImpl(
        private val typeParameter: TypeParameterDescriptor
) : TypeProjectionBase() {
    override fun isStarProjection() = true

    override fun getProjectionKind() = Variance.OUT_VARIANCE

    // No synchronization here: there's no problem in accidentally computing this twice
    private val _type: KotlinType by lazy(LazyThreadSafetyMode.PUBLICATION) {
        typeParameter.starProjectionType()
    }

    override fun getType() = _type
}

fun TypeParameterDescriptor.starProjectionType(): KotlinType {
    val classDescriptor = this.containingDeclaration as ClassifierDescriptorWithTypeParameters
    val typeParameters = classDescriptor.typeConstructor.parameters.map { it.typeConstructor }
    return TypeSubstitutor.create(
            object : TypeConstructorSubstitution() {
                override fun get(key: TypeConstructor) =
                        if (key in typeParameters)
                            TypeUtils.makeStarProjection(key.declarationDescriptor as TypeParameterDescriptor)
                        else null

            }
    ).substitute(this.upperBounds.first(), Variance.OUT_VARIANCE) ?: builtIns.defaultBound
}

class TypeBasedStarProjectionImpl(
        private val _type: KotlinType
) : TypeProjectionBase() {
    override fun isStarProjection() = true

    override fun getProjectionKind() = Variance.OUT_VARIANCE

    override fun getType() = _type
}
