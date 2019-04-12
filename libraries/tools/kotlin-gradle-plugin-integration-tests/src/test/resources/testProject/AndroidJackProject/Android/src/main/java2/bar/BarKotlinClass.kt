/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package bar

import foo.*

class BarKotlinClass {
    fun f() {
        BarJavaClass()
        FooJavaClass()
        FooKotlinClass()
    }
}