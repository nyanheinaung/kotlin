/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.structure

import org.jetbrains.kotlin.load.java.structure.JavaArrayType
import java.lang.reflect.GenericArrayType
import java.lang.reflect.Type

class ReflectJavaArrayType(override val reflectType: Type) : ReflectJavaType(), JavaArrayType {
    override val componentType: ReflectJavaType = with(reflectType) {
        when {
            this is GenericArrayType -> create(genericComponentType)
            this is Class<*> && isArray() -> create(getComponentType())
            else -> throw IllegalArgumentException("Not an array type (${reflectType::class.java}): $reflectType")
        }
    }
}
