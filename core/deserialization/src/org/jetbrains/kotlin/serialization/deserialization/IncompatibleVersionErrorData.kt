/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.deserialization

import org.jetbrains.kotlin.metadata.deserialization.BinaryVersion
import org.jetbrains.kotlin.name.ClassId

data class IncompatibleVersionErrorData<out T : BinaryVersion>(
    val actualVersion: T,
    val expectedVersion: T,
    val filePath: String,
    val classId: ClassId
)
