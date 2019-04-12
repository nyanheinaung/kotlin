/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.backend.ast.metadata

abstract class HasMetadata {
    private val metadata: MutableMap<String, Any?> = hashMapOf()

    fun <T> getData(key: String): T {
        @Suppress("UNCHECKED_CAST")
        return metadata[key] as T
    }

    fun <T> setData(key: String, value: T) {
        metadata[key] = value
    }

    fun hasData(key: String): Boolean {
        return metadata.containsKey(key)
    }

    fun removeData(key: String) {
        metadata.remove(key)
    }

    fun copyMetadataFrom(other: HasMetadata) {
        metadata.putAll(other.metadata)
    }
}
