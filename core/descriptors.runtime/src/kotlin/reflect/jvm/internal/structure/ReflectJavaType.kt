/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.structure

import org.jetbrains.kotlin.load.java.structure.JavaType
import java.lang.reflect.GenericArrayType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

abstract class ReflectJavaType : JavaType {
    protected abstract val reflectType: Type

    companion object Factory {
        fun create(type: Type): ReflectJavaType {
            return when {
                type is Class<*> && type.isPrimitive -> ReflectJavaPrimitiveType(type)
                type is GenericArrayType || type is Class<*> && type.isArray -> ReflectJavaArrayType(type)
                type is WildcardType -> ReflectJavaWildcardType(type)
                else -> ReflectJavaClassifierType(type)
            }
        }
    }

    override fun equals(other: Any?) = other is ReflectJavaType && reflectType == other.reflectType

    override fun hashCode() = reflectType.hashCode()

    override fun toString() = this::class.java.name + ": " + reflectType
}
