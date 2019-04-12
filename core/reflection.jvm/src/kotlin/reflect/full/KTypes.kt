/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:JvmName("KTypes")
package kotlin.reflect.full

import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.types.isFlexible
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf
import kotlin.reflect.*
import kotlin.reflect.jvm.internal.KTypeImpl

/**
 * Returns a new type with the same classifier, arguments and annotations as the given type, and with the given nullability.
 */
@SinceKotlin("1.1")
fun KType.withNullability(nullable: Boolean): KType {
    if (isMarkedNullable) {
        return if (nullable) this else KTypeImpl(TypeUtils.makeNotNullable((this as KTypeImpl).type)) { javaType }
    }

    // If the type is not marked nullable, it's either a non-null type or a platform type.
    val kotlinType = (this as KTypeImpl).type
    if (kotlinType.isFlexible()) return KTypeImpl(TypeUtils.makeNullableAsSpecified(kotlinType, nullable)) { javaType }

    return if (!nullable) this else KTypeImpl(TypeUtils.makeNullable(kotlinType)) { javaType }
}


/**
 * Returns `true` if `this` type is the same or is a subtype of [other], `false` otherwise.
 */
@SinceKotlin("1.1")
fun KType.isSubtypeOf(other: KType): Boolean {
    return (this as KTypeImpl).type.isSubtypeOf((other as KTypeImpl).type)
}

/**
 * Returns `true` if `this` type is the same or is a supertype of [other], `false` otherwise.
 */
@SinceKotlin("1.1")
fun KType.isSupertypeOf(other: KType): Boolean {
    return other.isSubtypeOf(this)
}
