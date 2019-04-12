/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.components

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.load.java.structure.JavaField
import org.jetbrains.kotlin.resolve.constants.ConstantValue
import org.jetbrains.kotlin.resolve.constants.ConstantValueFactory
import org.jetbrains.kotlin.types.KotlinType

object JavaPropertyInitializerEvaluatorImpl : JavaPropertyInitializerEvaluator {
    override fun getInitializerConstant(field: JavaField, descriptor: PropertyDescriptor): ConstantValue<*>? =
        field.initializerValue?.let { value -> convertLiteralValue(value, descriptor.type) }

    internal fun convertLiteralValue(value: Any, expectedType: KotlinType): ConstantValue<*>? =
        when (value) {
            // Note: `value` expression may be of class that does not match field type in some cases
            // tested for Int, left other checks just in case
            is Byte, is Short, is Int, is Long -> {
                ConstantValueFactory.createIntegerConstantValue((value as Number).toLong(), expectedType, false)
            }
            else -> {
                ConstantValueFactory.createConstantValue(value)
            }
        }
}
