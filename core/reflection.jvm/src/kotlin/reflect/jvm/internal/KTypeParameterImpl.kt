/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal

import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.types.Variance
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KVariance

internal class KTypeParameterImpl(override val descriptor: TypeParameterDescriptor) : KTypeParameter, KClassifierImpl {
    override val name: String
        get() = descriptor.name.asString()

    override val upperBounds: List<KType> by ReflectProperties.lazySoft {
        descriptor.upperBounds.map { kotlinType ->
            KTypeImpl(kotlinType) {
                TODO("Java type is not yet supported for type parameters: $descriptor")
            }
        }
    }

    override val variance: KVariance
        get() = when (descriptor.variance) {
            Variance.INVARIANT -> KVariance.INVARIANT
            Variance.IN_VARIANCE -> KVariance.IN
            Variance.OUT_VARIANCE -> KVariance.OUT
        }

    override val isReified: Boolean
        get() = descriptor.isReified

    override fun equals(other: Any?) =
        other is KTypeParameterImpl && descriptor == other.descriptor

    override fun hashCode() =
        descriptor.hashCode()

    override fun toString() =
        ReflectionObjectRenderer.renderTypeParameter(descriptor)
}
