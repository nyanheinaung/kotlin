/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.util.slicedMap

abstract class AbstractWritableSlice<K, V>(debugName: String) : KeyWithSlice<K, V, WritableSlice<K, V>>(debugName), WritableSlice<K, V> {
    override val slice: WritableSlice<K, V>
        get() = this

    override fun getKey(): AbstractWritableSlice<K, V> = this
}
