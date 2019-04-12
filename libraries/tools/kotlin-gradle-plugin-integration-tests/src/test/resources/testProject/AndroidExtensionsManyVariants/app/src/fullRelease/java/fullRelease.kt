/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.example.manyvariants

import android.app.Activity
import kotlinx.android.synthetic.full.activity_full.*
import kotlinx.android.synthetic.fullRelease.activity_full_release.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.release.activity_release.*

fun Activity.fullRelease() {
    viewMain
    viewFull
    viewRelease
    viewFullRelease
}
