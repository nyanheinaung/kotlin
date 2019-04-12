/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.kotlin.incremental.components

import java.io.Serializable

data class JvmPackagePartProto(val data: ByteArray, val strings: Array<String>) : Serializable

interface IncrementalCache {
    fun getObsoletePackageParts(): Collection<String>

    fun getObsoleteMultifileClasses(): Collection<String>

    fun getStableMultifileFacadeParts(facadeInternalName: String): Collection<String>?

    fun getPackagePartData(partInternalName: String): JvmPackagePartProto?

    fun getModuleMappingData(): ByteArray?

    fun getClassFilePath(internalClassName: String): String

    fun close()
}
