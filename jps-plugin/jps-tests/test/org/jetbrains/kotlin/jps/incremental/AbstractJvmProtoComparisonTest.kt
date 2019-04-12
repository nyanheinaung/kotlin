/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jps.incremental

import org.jetbrains.kotlin.incremental.LocalFileKotlinClass
import org.jetbrains.kotlin.incremental.ProtoData
import org.jetbrains.kotlin.incremental.storage.ProtoMapValue
import org.jetbrains.kotlin.incremental.toProtoData
import org.jetbrains.kotlin.load.kotlin.header.KotlinClassHeader
import org.jetbrains.kotlin.metadata.jvm.deserialization.BitEncoding
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.test.MockLibraryUtil
import java.io.File

abstract class AbstractJvmProtoComparisonTest : AbstractProtoComparisonTest<LocalFileKotlinClass>() {
    override fun compileAndGetClasses(sourceDir: File, outputDir: File): Map<ClassId, LocalFileKotlinClass> {
        MockLibraryUtil.compileKotlin(sourceDir.path, outputDir)

        val classFiles = outputDir.walkMatching { it.name.endsWith(".class") }
        val localClassFiles = classFiles.map { LocalFileKotlinClass.create(it)!! }
        return localClassFiles.associateBy { it.classId }
    }

    override fun LocalFileKotlinClass.toProtoData(): ProtoData? {
        assert(classHeader.metadataVersion.isCompatible()) { "Incompatible class ($classHeader): $location" }

        val bytes by lazy { BitEncoding.decodeBytes(classHeader.data!!) }
        val strings by lazy { classHeader.strings!! }
        val packageFqName = classId.packageFqName

        return when (classHeader.kind) {
            KotlinClassHeader.Kind.CLASS -> {
                ProtoMapValue(false, bytes, strings).toProtoData(packageFqName)
            }
            KotlinClassHeader.Kind.FILE_FACADE,
            KotlinClassHeader.Kind.MULTIFILE_CLASS_PART -> {
                ProtoMapValue(true, bytes, strings).toProtoData(packageFqName)
            }
            else -> {
                null
            }
        }
    }
}