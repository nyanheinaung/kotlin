/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package example

@example.ExampleAnnotation
public class TestClass {

    @example.ExampleAnnotation
    public val testVal: String = "text"

    @example.ExampleAnnotation
    public fun testFunction(): Class<*> = TestClassCustomized::class.java

}