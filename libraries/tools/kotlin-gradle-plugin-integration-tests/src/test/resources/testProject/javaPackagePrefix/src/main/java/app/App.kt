/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package my.pack.name.app

import my.pack.name.util.Appender
import my.pack.name.util.JUtil

class MyApp {
    fun method(arg: String): String {
        return JUtil.util() + Appender().append(arg)
    }
}
