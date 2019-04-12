/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:JvmName("KTypesJvm")

package kotlin.reflect.jvm

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind.ANNOTATION_CLASS
import org.jetbrains.kotlin.descriptors.ClassKind.INTERFACE
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.jvm.internal.KTypeImpl
import kotlin.reflect.jvm.internal.KotlinReflectionInternalError

/**
 * Returns the [KClass] instance representing the runtime class to which this type is erased to on JVM.
 */
@SinceKotlin("1.1")
val KType.jvmErasure: KClass<*>
    get() = classifier?.jvmErasure ?: throw KotlinReflectionInternalError("Cannot calculate JVM erasure for type: $this")

internal val KClassifier.jvmErasure: KClass<*>
    get() = when (this) {
        is KClass<*> -> this
        is KTypeParameter -> {
            // See getRepresentativeUpperBound in typeSignatureMapping.kt
            val bounds = upperBounds
            val representativeBound = bounds.firstOrNull {
                val classDescriptor = (it as KTypeImpl).type.constructor.declarationDescriptor as? ClassDescriptor
                classDescriptor != null && classDescriptor.kind != INTERFACE && classDescriptor.kind != ANNOTATION_CLASS
            } ?: bounds.firstOrNull()
            representativeBound?.jvmErasure ?: Any::class
        }
        else -> throw KotlinReflectionInternalError("Cannot calculate JVM erasure for type: $this")
    }
