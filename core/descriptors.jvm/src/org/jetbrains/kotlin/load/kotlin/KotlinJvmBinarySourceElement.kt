/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.kotlin

import org.jetbrains.kotlin.descriptors.SourceFile
import org.jetbrains.kotlin.metadata.jvm.deserialization.JvmMetadataVersion
import org.jetbrains.kotlin.serialization.deserialization.IncompatibleVersionErrorData
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedContainerSource

class KotlinJvmBinarySourceElement(
    val binaryClass: KotlinJvmBinaryClass,
    override val incompatibility: IncompatibleVersionErrorData<JvmMetadataVersion>? = null,
    override val isPreReleaseInvisible: Boolean = false
) : DeserializedContainerSource {
    override val presentableString: String
        get() = "Class '${binaryClass.classId.asSingleFqName().asString()}'"

    override fun getContainingFile(): SourceFile = SourceFile.NO_SOURCE_FILE

    override fun toString() = "${this::class.java.simpleName}: $binaryClass"
}
