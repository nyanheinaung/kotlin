/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.example.manyvariants

import android.app.Activity
import kotlinx.android.synthetic.debug.activity_debug.*
import kotlinx.android.synthetic.main.activity_main.*

fun Activity.debug() {
    viewMain
    viewDebug
}
