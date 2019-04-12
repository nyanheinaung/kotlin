/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.js

import org.jetbrains.kotlin.psi.KtFile

sealed class KotlinFileMetadata

data class KotlinPsiFileMetadata(val ktFile: KtFile) : KotlinFileMetadata()

data class KotlinDeserializedFileMetadata(
    val packageFragment: KotlinJavascriptPackageFragment,
    val fileId: Int
) : KotlinFileMetadata()
