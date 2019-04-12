/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package model

import annotation.ProcessThis

@ProcessThis
interface Model {

    var a: Int

    var b: Int
}

@ProcessThis
class Class {

    var a = 0
}