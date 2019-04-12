/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.quickfix

import com.android.sdklib.SdkVersionInfo.*


fun getVersionField(api: Int, fullyQualified: Boolean): String = getBuildCode(api)?.let {
    if (fullyQualified) "android.os.Build.VERSION_CODES.$it" else it
} ?: api.toString()