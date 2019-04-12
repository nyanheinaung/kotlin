/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package bar

import foo.A

@example.ExampleAnnotation
class B {
    @field:example.ExampleAnnotation
    val valB = "text"

    @example.ExampleAnnotation
    fun funB() {}

    fun useAfromB(a: A) {}
}