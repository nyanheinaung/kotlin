/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kclass

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

public class KInnerClass {
    @Retention(RetentionPolicy.RUNTIME)
    annotation class Foo

    inner class Inner(@Foo val foo: String){}
}
