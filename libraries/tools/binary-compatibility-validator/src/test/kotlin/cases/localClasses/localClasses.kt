/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package cases.localClasses

class A {
    fun a() : String {
        class B() {
            fun s() : String = "OK"

            inner class C {}

        }
        return B().s()
    }
}


class B {
    fun a(p: String) : String {
        class B() {
            fun s() : String = p
        }
        return B().s()
    }
}
