/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package com.github.frankiesardo.icepick

import android.os.Bundle

import icepick.Bundler

class MyBundler : Bundler<String> {
    override fun put(key: String, value: String?, bundle: Bundle) {
        if (value != null) {
            bundle.putString(key, value + "*")
        }
    }

    override fun get(key: String, bundle: Bundle): String? {
        return bundle.getString(key)
    }
}
