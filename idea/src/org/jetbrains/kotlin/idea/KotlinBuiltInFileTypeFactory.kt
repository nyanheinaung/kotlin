/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea

import com.intellij.openapi.fileTypes.FileTypeConsumer
import com.intellij.openapi.fileTypes.FileTypeFactory
import org.jetbrains.kotlin.idea.decompiler.builtIns.KotlinBuiltInFileType
import org.jetbrains.kotlin.serialization.deserialization.MetadataPackageFragment

class KotlinBuiltInFileTypeFactory : FileTypeFactory() {
    override fun createFileTypes(consumer: FileTypeConsumer) {
        consumer.consume(KotlinBuiltInFileType, KotlinBuiltInFileType.defaultExtension)
        consumer.consume(KotlinBuiltInFileType, MetadataPackageFragment.METADATA_FILE_EXTENSION)
    }
}
