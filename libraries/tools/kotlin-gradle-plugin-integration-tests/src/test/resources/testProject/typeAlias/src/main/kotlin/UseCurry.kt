/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package foo

class UseCurry {
    fun test() {
        val plus1 = Curry(Plus, 1)

        if (plus1(1) != 2) throw AssertionError()
    }

    private object Plus : FN2<Int, Int> {
        override fun invoke(p0: Int, p1: Int): Int =
                p0 + p1
    }
}