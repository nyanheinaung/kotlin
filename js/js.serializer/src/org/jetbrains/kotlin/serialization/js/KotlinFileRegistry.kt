/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.js

class KotlinFileRegistry {
    private val fileIdsImpl = mutableMapOf<KotlinFileMetadata, Int>()

    fun lookup(file: KotlinFileMetadata) = fileIdsImpl.getOrPut(file) { fileIdsImpl.size }

    val fileIds: Map<KotlinFileMetadata, Int>
        get() = fileIdsImpl
}
