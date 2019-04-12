/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package samples

import kotlin.test.assertEquals

typealias Sample = org.junit.Test
typealias RunWith = org.junit.runner.RunWith
typealias Enclosed = org.junit.experimental.runners.Enclosed

fun assertPrints(expression: Any?, expectedOutput: String) = assertEquals(expectedOutput, expression.toString())
