/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental.storage

import org.jetbrains.kotlin.incremental.dumpCollection
import org.jetbrains.kotlin.name.FqName
import java.io.File

internal open class ClassOneToManyMap(
        storageFile: File
) : BasicStringMap<Collection<String>>(storageFile, StringCollectionExternalizer) {
    override fun dumpValue(value: Collection<String>): String = value.dumpCollection()

    fun add(key: FqName, value: FqName) {
        storage.append(key.asString(), value.asString())
    }

    operator fun get(key: FqName): Collection<FqName> =
            storage[key.asString()]?.map(::FqName) ?: setOf()

    operator fun set(key: FqName, values: Collection<FqName>) {
        if (values.isEmpty()) {
            remove(key)
            return
        }

        storage[key.asString()] = values.map(FqName::asString)
    }

    fun remove(key: FqName) {
        storage.remove(key.asString())
    }

    fun removeValues(key: FqName, removed: Set<FqName>) {
        val notRemoved = this[key].filter { it !in removed }
        this[key] = notRemoved
    }
}

internal class SubtypesMap(storageFile: File) : ClassOneToManyMap(storageFile)
internal class SupertypesMap(storageFile: File) : ClassOneToManyMap(storageFile)
