/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package test

import example.MyProductionClass
import kotlin.test.Test

class MainIT {
    @Test
    fun mySimpleTest() {
        listOf(1, 2, 3).forEach { // check that stdlib is available
            MyProductionClass().i = it // check that production code is available
        }
    }
}
