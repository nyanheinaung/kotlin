/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.model

import org.jetbrains.kotlin.contracts.model.structure.*


interface ESExpressionVisitor<out T> {
    fun visitIs(isOperator: ESIs): T
    fun visitEqual(equal: ESEqual): T
    fun visitAnd(and: ESAnd): T
    fun visitNot(not: ESNot): T
    fun visitOr(or: ESOr): T

    fun visitVariable(esVariable: ESVariable): T
    fun visitConstant(esConstant: ESConstant): T

    fun visitReceiver(esReceiver: ESReceiver): T
}