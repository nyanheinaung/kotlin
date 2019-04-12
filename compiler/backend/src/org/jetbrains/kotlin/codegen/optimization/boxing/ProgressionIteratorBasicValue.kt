/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.optimization.boxing

import org.jetbrains.kotlin.codegen.AsmUtil
import org.jetbrains.kotlin.codegen.optimization.common.StrictBasicValue
import org.jetbrains.kotlin.codegen.range.*
import org.jetbrains.org.objectweb.asm.Type

class ProgressionIteratorBasicValue
private constructor(
    val nextMethodName: String,
    iteratorType: Type,
    private val primitiveElementType: Type,
    val boxedElementType: Type
) : StrictBasicValue(iteratorType) {

    private constructor(typeName: String, valuesPrimitiveType: Type, valuesBoxedType: Type = AsmUtil.boxType(valuesPrimitiveType)) :
            this("next$typeName", Type.getObjectType("kotlin/collections/${typeName}Iterator"), valuesPrimitiveType, valuesBoxedType)

    val nextMethodDesc: String
        get() = "()" + primitiveElementType.descriptor

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        if (!super.equals(other)) return false
        val value = other as ProgressionIteratorBasicValue
        return primitiveElementType == value.primitiveElementType
    }

    override fun hashCode(): Int =
        super.hashCode() * 31 + nextMethodName.hashCode()

    companion object {
        private val CHAR_PROGRESSION_ITERATOR_VALUE = ProgressionIteratorBasicValue("Char", Type.CHAR_TYPE)
        private val INT_PROGRESSION_ITERATOR_VALUE = ProgressionIteratorBasicValue("Int", Type.INT_TYPE)
        private val LONG_PROGRESSION_ITERATOR_VALUE = ProgressionIteratorBasicValue("Long", Type.LONG_TYPE)

        private val UINT_PROGRESSION_ITERATOR_VALUE =
            ProgressionIteratorBasicValue("UInt", Type.INT_TYPE, Type.getObjectType("kotlin/UInt"))
        private val ULONG_PROGRESSION_ITERATOR_VALUE =
            ProgressionIteratorBasicValue("ULong", Type.LONG_TYPE, Type.getObjectType("kotlin/ULong"))

        private val PROGRESSION_CLASS_NAME_TO_ITERATOR_VALUE: Map<String, ProgressionIteratorBasicValue> =
            hashMapOf(
                CHAR_RANGE_FQN to CHAR_PROGRESSION_ITERATOR_VALUE,
                CHAR_PROGRESSION_FQN to CHAR_PROGRESSION_ITERATOR_VALUE,
                INT_RANGE_FQN to INT_PROGRESSION_ITERATOR_VALUE,
                INT_PROGRESSION_FQN to INT_PROGRESSION_ITERATOR_VALUE,
                LONG_RANGE_FQN to LONG_PROGRESSION_ITERATOR_VALUE,
                LONG_PROGRESSION_FQN to LONG_PROGRESSION_ITERATOR_VALUE,
                UINT_RANGE_FQN to UINT_PROGRESSION_ITERATOR_VALUE,
                UINT_PROGRESSION_FQN to UINT_PROGRESSION_ITERATOR_VALUE,
                ULONG_RANGE_FQN to ULONG_PROGRESSION_ITERATOR_VALUE,
                ULONG_PROGRESSION_FQN to ULONG_PROGRESSION_ITERATOR_VALUE
            )

        fun byProgressionClassType(progressionClassType: Type): ProgressionIteratorBasicValue? =
            PROGRESSION_CLASS_NAME_TO_ITERATOR_VALUE[progressionClassType.className]
    }
}

