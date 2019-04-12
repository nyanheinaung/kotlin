/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package com.example.dagger.kotlin

import android.app.Activity
import android.os.Bundle

abstract class DemoActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Perform injection so that when this call returns all dependencies will be available for use.
        (application as DemoApplication).component.inject(this)
    }
}
