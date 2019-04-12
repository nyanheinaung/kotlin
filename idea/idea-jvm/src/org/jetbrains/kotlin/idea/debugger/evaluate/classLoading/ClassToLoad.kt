/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.debugger.evaluate.classLoading

import org.jetbrains.kotlin.idea.debugger.evaluate.GENERATED_CLASS_NAME

@Suppress("ArrayInDataClass")
data class ClassToLoad(val className: String, val relativeFileName: String, val bytes: ByteArray) {
    val isMainClass: Boolean
        get() = className == GENERATED_CLASS_NAME
}