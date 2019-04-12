/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.dsl

import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.junit.Assert.assertEquals
import org.junit.Test

class KotlinJvmOptionsTest {
    @Test
    fun testFreeArguments() {
        val options = KotlinJvmOptionsImpl()
        options.freeCompilerArgs = listOf(
            "-Xreport-perf",
            "-Xallow-kotlin-package",
            "-Xmultifile-parts-inherit",
            "-Xdump-declarations-to", "declarationsPath",
            "-script-templates", "a,b,c"
        )

        val arguments = K2JVMCompilerArguments()
        options.updateArguments(arguments)
        assertEquals(options.freeCompilerArgs, arguments.freeArgs)
    }
}