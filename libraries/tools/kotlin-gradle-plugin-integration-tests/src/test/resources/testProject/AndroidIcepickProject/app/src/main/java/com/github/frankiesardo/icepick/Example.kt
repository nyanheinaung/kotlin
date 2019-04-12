/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package com.github.frankiesardo.icepick

import org.parceler.Parcel

@Parcel
class Example {
    lateinit var name: String

    @JvmField
    var age: Int = 0

    constructor() { /*Required empty bean constructor*/
    }

    constructor(age: Int, name: String) {
        this.age = age
        this.name = name
    }
}