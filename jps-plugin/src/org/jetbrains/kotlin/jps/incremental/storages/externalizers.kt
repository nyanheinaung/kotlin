/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jps.incremental.storages

import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.io.IOUtil
import com.intellij.util.io.KeyDescriptor
import gnu.trove.THashSet
import org.jetbrains.jps.incremental.storage.PathStringDescriptor
import org.jetbrains.kotlin.incremental.storage.CollectionExternalizer
import java.io.DataInput
import java.io.DataOutput

object PathFunctionPairKeyDescriptor : KeyDescriptor<PathFunctionPair> {
    override fun read(input: DataInput): PathFunctionPair {
        val path = IOUtil.readUTF(input)
        val function = IOUtil.readUTF(input)
        return PathFunctionPair(path, function)
    }

    override fun save(output: DataOutput, value: PathFunctionPair) {
        IOUtil.writeUTF(output, value.path)
        IOUtil.writeUTF(output, value.function)
    }

    override fun getHashCode(value: PathFunctionPair): Int = value.hashCode()

    override fun isEqual(val1: PathFunctionPair, val2: PathFunctionPair): Boolean = val1 == val2
}

object PathCollectionExternalizer : CollectionExternalizer<String>(PathStringDescriptor(), { THashSet(FileUtil.PATH_HASHING_STRATEGY) })
