/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.test.kotlin

import org.junit.Test

class KotlinUastTypesTest : AbstractKotlinTypesTest() {
    @Test fun testLocalDeclarations() = doTest("LocalDeclarations")

    @Test fun testUnexpectedContainerException() = doTest("UnexpectedContainerException")

    @Test fun testCycleInTypeParameters() = doTest("CycleInTypeParameters")

    @Test fun testEa101715() = doTest("ea101715")

    @Test fun testStringTemplate() = doTest("StringTemplate")

    @Test fun testStringTemplateComplex() = doTest("StringTemplateComplex")
}