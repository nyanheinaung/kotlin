/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package com.example.databinding

import android.content.res.Resources
import android.databinding.BaseObservable

class UserProfile : BaseObservable() {
    open var gender: Gender = Gender.Female
}

enum class Gender(val display: String): Displayable {
    Male("male"), Female("female");
    override fun displayString(res: Resources): String = display
}