/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.decompiler.js

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.serialization.js.KotlinJavascriptSerializationUtil

object KotlinJavaScriptMetaFileType : FileType {

    override fun getName() = "KJSM"

    override fun getDescription() = "Kotlin JavaScript meta file"

    override fun getDefaultExtension() = KotlinJavascriptSerializationUtil.CLASS_METADATA_FILE_EXTENSION

    override fun getIcon() = null

    override fun isBinary() = true

    override fun isReadOnly() = true

    override fun getCharset(file: VirtualFile, content: ByteArray) = null
}
