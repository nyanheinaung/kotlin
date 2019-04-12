/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental.storage

import org.jetbrains.kotlin.incremental.dumpCollection
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.jvm.JvmClassName
import java.io.File

internal class SourceToJvmNameMap(storageFile: File) : AbstractSourceToOutputMap<JvmClassName>(JvmClassNameTransformer, storageFile)
internal class SourceToFqNameMap(storageFile: File) : AbstractSourceToOutputMap<FqName>(FqNameTransformer, storageFile)

internal abstract class AbstractSourceToOutputMap<Name>(
        private val nameTransformer: NameTransformer<Name>,
        storageFile: File
) : BasicStringMap<Collection<String>>(storageFile, PathStringDescriptor, StringCollectionExternalizer) {
    fun clearOutputsForSource(sourceFile: File) {
        remove(sourceFile.absolutePath)
    }

    fun add(sourceFile: File, className: Name) {
        storage.append(sourceFile.absolutePath, nameTransformer.asString(className))
    }

    fun contains(sourceFile: File): Boolean =
            sourceFile.absolutePath in storage

    operator fun get(sourceFile: File): Collection<Name> =
            storage[sourceFile.absolutePath].orEmpty().map(nameTransformer::asName)

    fun getFqNames(sourceFile: File): Collection<FqName> =
        storage[sourceFile.absolutePath].orEmpty().map(nameTransformer::asFqName)

    override fun dumpValue(value: Collection<String>) =
            value.dumpCollection()

    private fun remove(path: String) {
        storage.remove(path)
    }
}