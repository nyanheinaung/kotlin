/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package cases.private

// properties
private val privateVal: Any? = 1
private var privateVar: Any? = 1

// constants

private const val privateConst: Int = 4

// fun

@Suppress("UNUSED_PARAMETER")
private fun privateFun(a: Any?) = privateConst

// access
private class PrivateClassInPart {
    internal fun accessUsage() {
        privateFun(privateVal)
        privateFun(privateVar)
        privateFun(privateConst)
    }

}