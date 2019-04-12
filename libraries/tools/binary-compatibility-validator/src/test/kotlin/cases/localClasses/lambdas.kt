/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package cases.localClasses


class L {
    internal fun a(lambda: () -> Unit) = lambda()

    @Suppress("NOTHING_TO_INLINE")
    internal inline fun inlineLambda() {
        a {
            println("OK")
        }
    }
}

fun box() {
    L().inlineLambda()
}


// TODO: inline lambda from stdlib