/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.components

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.load.java.structure.JavaField
import org.jetbrains.kotlin.resolve.constants.ConstantValue

interface JavaPropertyInitializerEvaluator {
    fun getInitializerConstant(field: JavaField, descriptor: PropertyDescriptor): ConstantValue<*>?

    object DoNothing : JavaPropertyInitializerEvaluator {
        override fun getInitializerConstant(field: JavaField, descriptor: PropertyDescriptor): ConstantValue<*>? = null
    }
}
