/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package demo

internal val CONST = "CONST"

open class PublicClass {
    internal fun foo(): String = "foo"
    internal val bar: String = "bar"
    open internal fun baz(): String = "PublicClass.baz()"
}

internal data class InternalDataClass(val x: Int, val y: Int)

internal fun box(): String {
    return "OK"
}




