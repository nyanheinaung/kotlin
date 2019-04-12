/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental.storage

import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import org.jetbrains.annotations.TestOnly
import org.jetbrains.kotlin.utils.Printer
import java.io.File

abstract class BasicMap<K : Comparable<K>, V>(
        storageFile: File,
        keyDescriptor: KeyDescriptor<K>,
        valueExternalizer: DataExternalizer<V>
) {
    protected val storage = LazyStorage(storageFile, keyDescriptor, valueExternalizer)

    fun clean() {
        storage.clean()
    }

    fun flush(memoryCachesOnly: Boolean) {
        storage.flush(memoryCachesOnly)
    }

    fun close() {
        storage.close()
    }

    @TestOnly
    fun dump(): String {
        return with(StringBuilder()) {
            with(Printer(this)) {
                println(this@BasicMap::class.java.simpleName)
                pushIndent()

                for (key in storage.keys.sorted()) {
                    println("${dumpKey(key)} -> ${dumpValue(storage[key]!!)}")
                }

                popIndent()
            }

            this
        }.toString()
    }

    @TestOnly
    protected abstract fun dumpKey(key: K): String

    @TestOnly
    protected abstract fun dumpValue(value: V): String
}

abstract class BasicStringMap<V>(
        storageFile: File,
        keyDescriptor: KeyDescriptor<String>,
        valueExternalizer: DataExternalizer<V>
) : BasicMap<String, V>(storageFile, keyDescriptor, valueExternalizer) {
    constructor(
            storageFile: File,
            valueExternalizer: DataExternalizer<V>
    ) : this(storageFile, EnumeratorStringDescriptor.INSTANCE, valueExternalizer)

    override fun dumpKey(key: String): String = key
}
