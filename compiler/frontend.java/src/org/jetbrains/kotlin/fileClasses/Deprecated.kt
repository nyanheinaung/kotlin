/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:JvmName("FileClasses")

package org.jetbrains.kotlin.fileClasses

import org.jetbrains.kotlin.psi.KtFile

@Deprecated("Use JvmFileClassUtil instead.", level = DeprecationLevel.ERROR)
open class JvmFileClassesProvider

@Suppress("DEPRECATION_ERROR")
@Deprecated("Use JvmFileClassUtil instead.", level = DeprecationLevel.ERROR)
object NoResolveFileClassesProvider : JvmFileClassesProvider()

@Suppress("DEPRECATION_ERROR", "unused")
@Deprecated("Use JvmFileClassUtil.getFileClassInternalName instead.", ReplaceWith("JvmFileClassUtil.getFileClassInternalName(file)"), DeprecationLevel.ERROR)
fun JvmFileClassesProvider.getFileClassInternalName(file: KtFile): String =
        JvmFileClassUtil.getFileClassInternalName(file)
