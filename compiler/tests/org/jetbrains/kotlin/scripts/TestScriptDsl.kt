/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("unused")

package org.jetbrains.kotlin.scripts

open class TestDSLClass

fun TestDSLClass.fibCombine(f: (Int) -> Int, n: Int) = if (n < 2) 1 else f(n - 1) + f(n - 2)

interface TestDSLInterface

fun TestDSLInterface.fibCombine(f: (Int) -> Int, n: Int) = if (n < 2) 1 else f(n - 1) + f(n - 2)

open class TestDSLClassWithParam(val offset: Int)

fun TestDSLClassWithParam.fibCombine(f: (Int) -> Int, n: Int) = if (n < 2) offset else f(n - 1) + f(n - 2)

open class TestClassWithOverridableProperty(open val num: Int)