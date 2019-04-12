/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

import kotlin.test.*

import com.example.exported

@Test
fun foo() {
    val exp = exported()
    assertTrue(exp % 7 == 0, "Not divisible by 7")
    println("tests.foo: exp = $exp")
}