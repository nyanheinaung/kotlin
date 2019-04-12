/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package com.example.databinding

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import com.example.databinding.databinding.ActivityTestBinding

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityTestBinding = DataBindingUtil.setContentView(this, R.layout.activity_test)
        val uprof = UserProfile()
        binding.userProfile = uprof
        binding.context = this
        binding.genderPicker.adapter = EnumAdapter(this, Gender::class.java)
        binding.genderPicker.setOnTouchListener { v, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN)
                spinnerClicked = true
            false
        }
    }

    var spinnerClicked = false
    fun spinnerClicked() {
        spinnerClicked = true
    }
    fun selectionChanged() {
        if (spinnerClicked)
            Toast.makeText(this, "sdds", Toast.LENGTH_LONG).show()
        spinnerClicked = false
    }

    fun clickHandler(uprof: UserProfile): Boolean = true

    fun longClickHandler(uprof: UserProfile): Boolean = true
}