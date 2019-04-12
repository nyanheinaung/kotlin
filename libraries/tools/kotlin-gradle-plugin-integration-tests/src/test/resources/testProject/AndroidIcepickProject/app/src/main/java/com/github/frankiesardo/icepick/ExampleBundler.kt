/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package com.github.frankiesardo.icepick

import android.os.Bundle
import android.os.Parcelable

import org.parceler.Parcels

import icepick.Bundler

class ExampleBundler : Bundler<Any> {
    override fun put(s: String, example: Any, bundle: Bundle) {
        bundle.putParcelable(s, Parcels.wrap(example))
    }

    override fun get(s: String, bundle: Bundle): Any {
        return Parcels.unwrap<Any>(bundle.getParcelable<Parcelable>(s))
    }
}
