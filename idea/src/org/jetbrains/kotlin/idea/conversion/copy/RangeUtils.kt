/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.conversion.copy

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

//NOTE: should be moved to some "util" place

val TextRange.start: Int
    get() = startOffset

val TextRange.end: Int
    get() = endOffset

val PsiElement.range: TextRange
    get() = textRange!!

val RangeMarker.range: TextRange?
    get() = if (isValid) {
        val start = startOffset
        val end = endOffset
        if (start in 0..end) {
            TextRange(start, end)
        } else {
            // Probably a race condition had happened
            LOG.error("Invalid range [$start, $end] for range marker (valid = $isValid)")
            null
        }
    } else null

private val LOG = Logger.getInstance("RangeUtils")
