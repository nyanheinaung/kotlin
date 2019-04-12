/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.decompiler.builtIns

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.idea.KotlinIcons
import org.jetbrains.kotlin.serialization.deserialization.builtins.BuiltInSerializerProtocol

object KotlinBuiltInFileType : FileType {
    override fun getName() = "kotlin_builtins"

    override fun getDescription() = "Kotlin built-in declarations"

    override fun getDefaultExtension() = BuiltInSerializerProtocol.BUILTINS_FILE_EXTENSION

    override fun getIcon() = KotlinIcons.FILE

    override fun isBinary() = true

    override fun isReadOnly() = true

    override fun getCharset(file: VirtualFile, content: ByteArray) = null
}
