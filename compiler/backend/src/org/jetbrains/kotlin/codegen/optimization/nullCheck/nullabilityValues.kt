/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.optimization.nullCheck

import org.jetbrains.kotlin.codegen.optimization.boxing.ProgressionIteratorBasicValue
import org.jetbrains.kotlin.codegen.optimization.common.StrictBasicValue
import org.jetbrains.kotlin.resolve.jvm.AsmTypes
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.tree.analysis.BasicValue

class NotNullBasicValue(type: Type?) : StrictBasicValue(type) {
    override fun equals(other: Any?): Boolean = other is NotNullBasicValue && other.type == type
    // We do not differ not-nullable values, so we should always return the same hashCode
    // Actually it doesn't really matter because analyzer is not supposed to store values in hashtables
    override fun hashCode() = 0

    companion object {
        val NOT_NULL_REFERENCE_VALUE = NotNullBasicValue(StrictBasicValue.REFERENCE_VALUE.type)
    }
}

object NullBasicValue : StrictBasicValue(AsmTypes.OBJECT_TYPE)

enum class Nullability {
    NULL, NOT_NULL, NULLABLE;

    fun isNull() = this == NULL
    fun isNotNull() = this == NOT_NULL
}

fun BasicValue.getNullability(): Nullability =
    when (this) {
        is NullBasicValue -> Nullability.NULL
        is NotNullBasicValue -> Nullability.NOT_NULL
        is ProgressionIteratorBasicValue -> Nullability.NOT_NULL
        else -> Nullability.NULLABLE
    }