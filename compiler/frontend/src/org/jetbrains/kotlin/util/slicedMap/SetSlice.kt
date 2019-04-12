/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.util.slicedMap


open class SetSlice<K> @JvmOverloads constructor(rewritePolicy: RewritePolicy, isCollective: Boolean = false) :
    BasicWritableSlice<K, Boolean>(rewritePolicy, isCollective) {
    companion object {
        @JvmField
        val DEFAULT = false
    }

    override fun check(key: K, value: Boolean?): Boolean {
        assert(value != null) { this.toString() + " called with null value" }
        return value != DEFAULT
    }

    override fun computeValue(map: SlicedMap?, key: K, value: Boolean?, valueNotFound: Boolean): Boolean? {
        val result = super.computeValue(map, key, value, valueNotFound)
        return result ?: DEFAULT
    }

    override fun makeRawValueVersion(): ReadOnlySlice<K, Boolean>? {
        return object : DelegatingSlice<K, Boolean>(this) {
            override fun computeValue(map: SlicedMap, key: K, value: Boolean?, valueNotFound: Boolean): Boolean? {
                if (valueNotFound) return DEFAULT
                return value
            }
        }
    }
}