/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

//@file:JvmName("ConstUtil")

package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.builtins.UnsignedTypes
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeUtils

object ConstUtil {
    @JvmStatic fun canBeUsedForConstVal(type: KotlinType) = type.canBeUsedForConstVal()
}

fun KotlinType.canBeUsedForConstVal() =
    (KotlinBuiltIns.isPrimitiveType(this) || UnsignedTypes.isUnsignedType(this)) && !TypeUtils.isNullableType(this) ||
            KotlinBuiltIns.isString(this)
