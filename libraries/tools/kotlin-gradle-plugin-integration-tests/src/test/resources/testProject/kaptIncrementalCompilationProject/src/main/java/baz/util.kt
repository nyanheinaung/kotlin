/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package baz

@field:example.ExampleAnnotation
val valUtil = 0

@example.ExampleAnnotation
fun funUtil() {}

fun notAnnotatedFun() {}

fun functionWithBody() {
    if (2 * 2 == 4) {
        // All's right
    }
}