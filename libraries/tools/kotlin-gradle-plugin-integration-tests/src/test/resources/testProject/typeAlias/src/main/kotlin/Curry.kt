/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package foo

typealias FN2<T, R> = (T, T) -> R

class Curry<T, R>(private val f: FN2<T, R>, private val arg1: T) {
    operator fun invoke(arg2: T): R =
            f(arg1, arg2)
}
