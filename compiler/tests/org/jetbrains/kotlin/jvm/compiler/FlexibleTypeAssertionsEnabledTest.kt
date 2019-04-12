/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jvm.compiler

import org.jetbrains.kotlin.builtins.DefaultBuiltIns
import org.jetbrains.kotlin.test.KotlinTestWithEnvironmentManagement
import org.jetbrains.kotlin.types.KotlinTypeFactory

class FlexibleTypeAssertionsEnabledTest : KotlinTestWithEnvironmentManagement() {

    fun testAssertionsAreOn() {
        val builtIns = DefaultBuiltIns.Instance

        try {
            KotlinTypeFactory.flexibleType(builtIns.intType, builtIns.stringType).arguments
        } catch (e: AssertionError) {
            assertEquals("Lower bound Int of a flexible type must be a subtype of the upper bound String", e.message)
            return
        }

        fail("Assertion error expected")
    }
}
