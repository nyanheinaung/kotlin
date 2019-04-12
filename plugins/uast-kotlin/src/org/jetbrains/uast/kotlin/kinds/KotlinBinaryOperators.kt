/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin

import org.jetbrains.uast.UastBinaryOperator

object KotlinBinaryOperators {
    @JvmField
    val IN = UastBinaryOperator("in")
    
    @JvmField
    val NOT_IN = UastBinaryOperator("!in")

    @JvmField
    val RANGE_TO = UastBinaryOperator("..")
}