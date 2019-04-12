/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.kotlin

import org.jetbrains.kotlin.load.java.structure.JavaClass
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.serialization.deserialization.KotlinMetadataFinder

interface KotlinClassFinder : KotlinMetadataFinder {
    fun findKotlinClassOrContent(classId: ClassId): Result?

    fun findKotlinClassOrContent(javaClass: JavaClass): Result?

    sealed class Result {
        fun toKotlinJvmBinaryClass(): KotlinJvmBinaryClass? = (this as? KotlinClass)?.kotlinJvmBinaryClass

        data class KotlinClass(val kotlinJvmBinaryClass: KotlinJvmBinaryClass) : Result()
        data class ClassFileContent(
            @Suppress("ArrayInDataClass")
            val content: ByteArray
        ) : Result()
    }
}

fun KotlinClassFinder.findKotlinClass(classId: ClassId): KotlinJvmBinaryClass? =
    findKotlinClassOrContent(classId)?.toKotlinJvmBinaryClass()

fun KotlinClassFinder.findKotlinClass(javaClass: JavaClass): KotlinJvmBinaryClass? =
    findKotlinClassOrContent(javaClass)?.toKotlinJvmBinaryClass()
