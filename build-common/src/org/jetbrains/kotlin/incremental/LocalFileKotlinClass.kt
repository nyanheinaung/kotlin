/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental

import org.jetbrains.kotlin.load.kotlin.FileBasedKotlinClass
import org.jetbrains.kotlin.load.kotlin.header.KotlinClassHeader
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.resolve.jvm.JvmClassName
import java.io.File

class LocalFileKotlinClass private constructor(
        private val file: File,
        private val fileContents: ByteArray,
        className: ClassId,
        classVersion: Int,
        classHeader: KotlinClassHeader,
        innerClasses: InnerClassesInfo
) : FileBasedKotlinClass(className, classVersion, classHeader, innerClasses) {

    companion object {
        fun create(file: File): LocalFileKotlinClass? {
            val fileContents = file.readBytes()
            return FileBasedKotlinClass.create(fileContents) {
                className, classVersion, classHeader, innerClasses ->
                LocalFileKotlinClass(file, fileContents, className, classVersion, classHeader, innerClasses)
            }
        }
    }

    val className: JvmClassName by lazy { JvmClassName.byClassId(classId) }

    override val location: String
        get() = file.absolutePath

    public override fun getFileContents(): ByteArray = fileContents

    override fun hashCode(): Int = file.hashCode()
    override fun equals(other: Any?): Boolean = other is LocalFileKotlinClass && file == other.file
    override fun toString(): String = "${this::class.java}: $file"
}
