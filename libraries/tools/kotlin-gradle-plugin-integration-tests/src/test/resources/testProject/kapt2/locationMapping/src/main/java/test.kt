/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package example

class Test {
    @field:example.GenError
    val a: String

    fun b(@example.GenError a: Boolean) {}
}