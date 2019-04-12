/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.scripts

import org.junit.Assert
import java.io.ByteArrayOutputStream
import java.io.PrintStream

internal const val NUM_4_LINE = "num: 4"

internal const val FIB_SCRIPT_OUTPUT_TAIL =
"""
fib(1)=1
fib(0)=1
fib(2)=2
fib(1)=1
fib(3)=3
fib(1)=1
fib(0)=1
fib(2)=2
fib(4)=5
"""

internal fun captureOut(body: () -> Unit): String {
    val outStream = ByteArrayOutputStream()
    val prevOut = System.out
    System.setOut(PrintStream(outStream))
    try {
        body()
    }
    finally {
        System.out.flush()
        System.setOut(prevOut)
    }
    return outStream.toString()
}

private fun String.linesSplitTrim() =
        split('\n','\r').map(String::trim).filter(String::isNotBlank)

internal fun assertEqualsTrimmed(expected: String, actual: String) =
        Assert.assertEquals(expected.linesSplitTrim(), actual.linesSplitTrim())
