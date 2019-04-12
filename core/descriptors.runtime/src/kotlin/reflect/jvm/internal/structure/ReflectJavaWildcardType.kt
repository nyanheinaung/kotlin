/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.structure

import org.jetbrains.kotlin.load.java.structure.JavaWildcardType
import java.lang.reflect.WildcardType

class ReflectJavaWildcardType(override val reflectType: WildcardType) : ReflectJavaType(), JavaWildcardType {
    override val bound: ReflectJavaType?
        get() {
            val upperBounds = reflectType.upperBounds
            val lowerBounds = reflectType.lowerBounds
            if (upperBounds.size > 1 || lowerBounds.size > 1) {
                throw UnsupportedOperationException("Wildcard types with many bounds are not yet supported: $reflectType")
            }
            return when {
                lowerBounds.size == 1 -> create(lowerBounds.single())
                upperBounds.size == 1 -> upperBounds.single().let { ub -> if (ub != Any::class.java) create(ub) else null }
                else -> null
            }
        }

    override val isExtends: Boolean
        get() = reflectType.upperBounds.firstOrNull() != Any::class.java
}
