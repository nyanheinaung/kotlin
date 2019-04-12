/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.model


/**
 * There is a subset of Kotlin language in Effect system (expressions
 *   in right hand side of conditional effect) and [ESExpression] with subtypes
 *   precisely enumerate what can be found here.
 */
interface ESExpression {
    fun <T> accept(visitor: ESExpressionVisitor<T>): T
}

interface ESOperator : ESExpression {
    /**
     * [Functor] that contains logic of concrete operator
     */
    val functor: Functor
}

interface ESValue : Computation, ESExpression