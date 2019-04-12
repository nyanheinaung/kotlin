/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental.storage

import com.intellij.util.io.BooleanDataDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.jvm.JvmClassName
import java.io.File

internal class DirtyClassesJvmNameMap(storageFile: File) : AbstractDirtyClassesMap<JvmClassName>(JvmClassNameTransformer, storageFile)
internal class DirtyClassesFqNameMap(storageFile: File) : AbstractDirtyClassesMap<FqName>(FqNameTransformer, storageFile)

internal abstract class AbstractDirtyClassesMap<Name>(
        private val nameTransformer: NameTransformer<Name>,
        storageFile: File
) : BasicStringMap<Boolean>(storageFile, BooleanDataDescriptor.INSTANCE) {
    fun markDirty(className: Name) {
        storage[nameTransformer.asString(className)] = true
    }

    fun notDirty(className: Name) {
        storage.remove(nameTransformer.asString(className))
    }

    fun getDirtyOutputClasses(): Collection<Name> =
            storage.keys.map { nameTransformer.asName(it) }

    fun isDirty(className: Name): Boolean =
            storage.contains(nameTransformer.asString(className))

    override fun dumpValue(value: Boolean) = ""
}
