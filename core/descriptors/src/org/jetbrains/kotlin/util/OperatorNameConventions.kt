/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.util

import org.jetbrains.kotlin.name.Name

object OperatorNameConventions {
    @JvmField val GET_VALUE = Name.identifier("getValue")
    @JvmField val SET_VALUE = Name.identifier("setValue")
    @JvmField val PROVIDE_DELEGATE = Name.identifier("provideDelegate")

    @JvmField val EQUALS = Name.identifier("equals")
    @JvmField val COMPARE_TO = Name.identifier("compareTo")
    @JvmField val CONTAINS = Name.identifier("contains")
    @JvmField val INVOKE = Name.identifier("invoke")
    @JvmField val ITERATOR = Name.identifier("iterator")
    @JvmField val GET = Name.identifier("get")
    @JvmField val SET = Name.identifier("set")
    @JvmField val NEXT = Name.identifier("next")
    @JvmField val HAS_NEXT = Name.identifier("hasNext")

    @JvmField val COMPONENT_REGEX = Regex("component\\d+")

    @JvmField val AND = Name.identifier("and")
    @JvmField val OR = Name.identifier("or")

    @JvmField val INC = Name.identifier("inc")
    @JvmField val DEC = Name.identifier("dec")
    @JvmField val PLUS = Name.identifier("plus")
    @JvmField val MINUS = Name.identifier("minus")
    @JvmField val NOT = Name.identifier("not")

    @JvmField val UNARY_MINUS = Name.identifier("unaryMinus")
    @JvmField val UNARY_PLUS = Name.identifier("unaryPlus")

    @JvmField val TIMES = Name.identifier("times")
    @JvmField val DIV = Name.identifier("div")
    @JvmField val MOD = Name.identifier("mod")
    @JvmField val REM = Name.identifier("rem")
    @JvmField val RANGE_TO = Name.identifier("rangeTo")

    @JvmField val TIMES_ASSIGN = Name.identifier("timesAssign")
    @JvmField val DIV_ASSIGN = Name.identifier("divAssign")
    @JvmField val MOD_ASSIGN = Name.identifier("modAssign")
    @JvmField val REM_ASSIGN = Name.identifier("remAssign")
    @JvmField val PLUS_ASSIGN = Name.identifier("plusAssign")
    @JvmField val MINUS_ASSIGN = Name.identifier("minusAssign")

    // If you add new unary, binary or assignment operators, add it to OperatorConventions as well

    @JvmField
    val UNARY_OPERATION_NAMES = setOf(INC, DEC, UNARY_PLUS, UNARY_MINUS, NOT)

    @JvmField
    internal val SIMPLE_UNARY_OPERATION_NAMES = setOf(UNARY_PLUS, UNARY_MINUS, NOT)

    @JvmField
    val BINARY_OPERATION_NAMES = setOf(TIMES, PLUS, MINUS, DIV, MOD, REM, RANGE_TO)

    @JvmField
    internal val ASSIGNMENT_OPERATIONS = setOf(TIMES_ASSIGN, DIV_ASSIGN, MOD_ASSIGN, REM_ASSIGN, PLUS_ASSIGN, MINUS_ASSIGN)
}
