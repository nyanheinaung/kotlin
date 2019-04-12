/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:JvmName("KClassifiers")
package kotlin.reflect.full

import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.types.*
import kotlin.reflect.*
import kotlin.reflect.jvm.internal.KClassifierImpl
import kotlin.reflect.jvm.internal.KTypeImpl
import kotlin.reflect.jvm.internal.KotlinReflectionInternalError

/**
 * Creates a [KType] instance with the given classifier, type arguments, nullability and annotations.
 * If the number of passed type arguments is not equal to the total number of type parameters of a classifier,
 * an exception is thrown. If any of the arguments does not satisfy the bounds of the corresponding type parameter,
 * an exception is thrown.
 *
 * For classifiers representing type parameters, the type argument list must always be empty.
 * For classes, the type argument list should contain arguments for the type parameters of the class. If the class is `inner`,
 * the list should follow with arguments for the type parameters of its outer class, and so forth until a class is
 * not `inner`, or is declared on the top level.
 */
@SinceKotlin("1.1")
fun KClassifier.createType(
        arguments: List<KTypeProjection> = emptyList(),
        nullable: Boolean = false,
        annotations: List<Annotation> = emptyList()
): KType {
    val descriptor = (this as? KClassifierImpl)?.descriptor
                     ?: throw KotlinReflectionInternalError("Cannot create type for an unsupported classifier: $this (${this.javaClass})")

    val typeConstructor = descriptor.typeConstructor
    val parameters = typeConstructor.parameters
    if (parameters.size != arguments.size) {
        throw IllegalArgumentException("Class declares ${parameters.size} type parameters, but ${arguments.size} were provided.")
    }

    // TODO: throw exception if argument does not satisfy bounds

    val typeAnnotations =
            if (annotations.isEmpty()) Annotations.EMPTY
            else Annotations.EMPTY // TODO: support type annotations

    val kotlinType = createKotlinType(typeAnnotations, typeConstructor, arguments, nullable)

    return KTypeImpl(kotlinType) {
        TODO("Java type is not yet supported for types created with createType (classifier = $this)")
    }
}

private fun createKotlinType(
        typeAnnotations: Annotations, typeConstructor: TypeConstructor, arguments: List<KTypeProjection>, nullable: Boolean
): SimpleType {
    val parameters = typeConstructor.parameters
    return KotlinTypeFactory.simpleType(typeAnnotations, typeConstructor, arguments.mapIndexed { index, typeProjection ->
        val type = (typeProjection.type as KTypeImpl?)?.type
        when (typeProjection.variance) {
            KVariance.INVARIANT -> TypeProjectionImpl(Variance.INVARIANT, type!!)
            KVariance.IN -> TypeProjectionImpl(Variance.IN_VARIANCE, type!!)
            KVariance.OUT -> TypeProjectionImpl(Variance.OUT_VARIANCE, type!!)
            null -> StarProjectionImpl(parameters[index])
        }
    }, nullable)
}

/**
 * Creates an instance of [KType] with the given classifier, substituting all its type parameters with star projections.
 * The resulting type is not marked as nullable and does not have any annotations.
 *
 * @see [KClassifier.createType]
 */
@SinceKotlin("1.1")
val KClassifier.starProjectedType: KType
    get() {
        val descriptor = (this as? KClassifierImpl)?.descriptor
                         ?: return createType()

        val typeParameters = descriptor.typeConstructor.parameters
        if (typeParameters.isEmpty()) return createType() // TODO: optimize, get defaultType from ClassDescriptor

        return createType(typeParameters.map { KTypeProjection.STAR })
    }
