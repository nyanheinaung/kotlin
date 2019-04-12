/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.codeFragmentUtil

import com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtCodeFragment
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile

val SUPPRESS_DIAGNOSTICS_IN_DEBUG_MODE: Key<Boolean> = Key.create<Boolean>("SUPPRESS_DIAGNOSTICS_IN_DEBUG_MODE")

fun KtElement.suppressDiagnosticsInDebugMode(): Boolean {
    return if (this is KtFile) {
        this.suppressDiagnosticsInDebugMode
    } else {
        val file = this.containingFile
        file is KtFile && file.suppressDiagnosticsInDebugMode
    }
}

var KtFile.suppressDiagnosticsInDebugMode: Boolean
    get() = when (this) {
        is KtCodeFragment -> true
        else -> getUserData(SUPPRESS_DIAGNOSTICS_IN_DEBUG_MODE) ?: false
    }
    set(skip) {
        putUserData(SUPPRESS_DIAGNOSTICS_IN_DEBUG_MODE, skip)
    }

val DEBUG_TYPE_REFERENCE_STRING: String = "DebugTypeKotlinRulezzzz"