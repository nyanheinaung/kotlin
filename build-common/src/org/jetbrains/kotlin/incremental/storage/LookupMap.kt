/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental.storage

import java.io.File

internal class LookupMap(storage: File) : BasicMap<LookupSymbolKey, Collection<Int>>(storage, LookupSymbolKeyDescriptor, IntCollectionExternalizer) {
    override fun dumpKey(key: LookupSymbolKey): String = key.toString()

    override fun dumpValue(value: Collection<Int>): String = value.toString()

    fun add(name: String, scope: String, fileId: Int) {
        storage.append(LookupSymbolKey(name, scope), fileId)
    }

    operator fun get(key: LookupSymbolKey): Collection<Int>? = storage[key]

    operator fun set(key: LookupSymbolKey, fileIds: Set<Int>) {
        storage[key] = fileIds
    }

    fun remove(key: LookupSymbolKey) {
        storage.remove(key)
    }

    val keys: Collection<LookupSymbolKey>
        get() = storage.keys
}
