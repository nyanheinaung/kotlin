/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.debugger.evaluate.classLoading

import org.jetbrains.kotlin.extensions.ProjectExtensionDescriptor

interface AndroidDexer {
    companion object : ProjectExtensionDescriptor<AndroidDexer>(
        "org.jetbrains.kotlin.androidDexer", AndroidDexer::class.java
    )

    fun dex(classes: Collection<ClassToLoad>): ByteArray?
}