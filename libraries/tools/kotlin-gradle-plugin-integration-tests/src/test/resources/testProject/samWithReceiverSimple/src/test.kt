/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package test

import lib.*

fun test() {
    val manager = Manager()
    manager.doJob { other -> println(this) }
    manager.doJobWithoutReceiver { context, other -> println(context) }
}