/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

class PublicClassHeir : PublicClass() {
    override internal fun baz(): String = "PublicClassHeir.baz()"
}

fun <T> assertEquals(e: T, a: T) {
    if (e != a) throw Exception("Expected: $e, actual: $a")
}

fun test() {
    assertEquals("CONST", CONST)

    assertEquals("foo", PublicClass().foo())
    assertEquals("bar", PublicClass().bar)
    assertEquals("PublicClass.baz()", PublicClass().baz())

    assertEquals("foo", PublicClassHeir().foo())
    assertEquals("bar", PublicClassHeir().bar)
    assertEquals("PublicClassHeir.baz()", PublicClassHeir().baz())

    val data = InternalDataClass(10, 20)
    assertEquals(10, data.x)
    assertEquals(20, data.y)

    assertEquals("OK", box())
}