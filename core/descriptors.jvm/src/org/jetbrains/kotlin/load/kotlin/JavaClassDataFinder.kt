/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.kotlin

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.serialization.deserialization.ClassData
import org.jetbrains.kotlin.serialization.deserialization.ClassDataFinder

class JavaClassDataFinder(
    internal val kotlinClassFinder: KotlinClassFinder,
    private val deserializedDescriptorResolver: DeserializedDescriptorResolver
) : ClassDataFinder {
    override fun findClassData(classId: ClassId): ClassData? {
        val kotlinClass = kotlinClassFinder.findKotlinClass(classId) ?: return null
        assert(kotlinClass.classId == classId) {
            "Class with incorrect id found: expected $classId, actual ${kotlinClass.classId}"
        }
        return deserializedDescriptorResolver.readClassData(kotlinClass)
    }
}
