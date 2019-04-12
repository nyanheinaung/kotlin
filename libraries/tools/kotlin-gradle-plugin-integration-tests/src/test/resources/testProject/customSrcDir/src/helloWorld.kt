/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package demo

import java.util.ArrayList

class KotlinGreetingJoiner() {

    val names = ArrayList<String?>()

    fun addName(name : String?): Unit{
        names.add(name)
    }
}

