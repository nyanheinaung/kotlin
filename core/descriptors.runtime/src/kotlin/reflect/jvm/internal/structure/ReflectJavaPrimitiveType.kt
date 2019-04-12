/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.structure

import org.jetbrains.kotlin.builtins.PrimitiveType
import org.jetbrains.kotlin.load.java.structure.JavaPrimitiveType
import org.jetbrains.kotlin.resolve.jvm.JvmPrimitiveType

class ReflectJavaPrimitiveType(override val reflectType: Class<*>) : ReflectJavaType(), JavaPrimitiveType {
    override val type: PrimitiveType?
        get() = if (reflectType == Void.TYPE)
            null
        else
            JvmPrimitiveType.get(reflectType.name).primitiveType
}
