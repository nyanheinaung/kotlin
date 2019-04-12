/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.internal

import com.intellij.openapi.components.ServiceManager
import org.jetbrains.kotlin.psi.KtFile

class DecompileFailedException(message: String, cause: Throwable) : RuntimeException(message, cause)

interface KotlinDecompilerService {
    fun decompile(file: KtFile): String?

    companion object {
        fun getInstance(): KotlinDecompilerService? {
            return ServiceManager.getService(KotlinDecompilerService::class.java)
        }
    }
}

