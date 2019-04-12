/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js

import org.jetbrains.kotlin.name.FqName

enum class PredefinedAnnotation(fqName: String) {
    LIBRARY("kotlin.js.library"),
    NATIVE("kotlin.js.native"),
    NATIVE_INVOKE("kotlin.js.nativeInvoke"),
    NATIVE_GETTER("kotlin.js.nativeGetter"),
    NATIVE_SETTER("kotlin.js.nativeSetter");

    val fqName: FqName = FqName(fqName)

    companion object {
        val WITH_CUSTOM_NAME = setOf(LIBRARY, NATIVE)
    }
}
