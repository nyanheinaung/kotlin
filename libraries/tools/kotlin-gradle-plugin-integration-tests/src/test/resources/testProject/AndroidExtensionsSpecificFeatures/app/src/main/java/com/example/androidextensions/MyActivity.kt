/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package com.example.androidextensions

import android.os.Bundle
import android.app.Activity
import kotlinx.android.synthetic.main.activity_main.textView
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

class HomeActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        this.textView.setText("Hello, world!")
    }
}

@Parcelize
class User(val firstName: String, val lastName: String) : Parcelable