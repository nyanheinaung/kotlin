/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.interpretation

import org.jetbrains.kotlin.contracts.description.expressions.BooleanConstantReference
import org.jetbrains.kotlin.contracts.description.expressions.ConstantReference
import org.jetbrains.kotlin.contracts.model.structure.ESConstant
import org.jetbrains.kotlin.contracts.model.structure.ESConstants

internal class ConstantValuesInterpreter {
    fun interpretConstant(constantReference: ConstantReference, constants: ESConstants): ESConstant? = when (constantReference) {
        BooleanConstantReference.TRUE -> constants.trueValue
        BooleanConstantReference.FALSE -> constants.falseValue
        ConstantReference.NULL -> constants.nullValue
        ConstantReference.NOT_NULL -> constants.notNullValue
        ConstantReference.WILDCARD -> constants.wildcard
        else -> null
    }
}
