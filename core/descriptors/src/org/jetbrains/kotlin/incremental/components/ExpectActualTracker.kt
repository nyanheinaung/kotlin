/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental.components

import org.jetbrains.kotlin.container.DefaultImplementation
import java.io.File

@DefaultImplementation(ExpectActualTracker.DoNothing::class)
interface ExpectActualTracker {
    fun report(expectedFile: File, actualFile: File)

    object DoNothing : ExpectActualTracker {
        override fun report(expectedFile: File, actualFile: File) {
        }
    }
}