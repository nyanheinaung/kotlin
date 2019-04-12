/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.j2k

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.psi.KtFile

class AfterConversionPass(val project: Project, val postProcessor: PostProcessor) {
    fun run(kotlinFile: KtFile, range: TextRange?) {
        val rangeMarker = if (range != null) {
            val document = kotlinFile.viewProvider.document!!
            val marker = document.createRangeMarker(range.startOffset, range.endOffset)
            marker.isGreedyToLeft = true
            marker.isGreedyToRight = true
            marker
        } else {
            null
        }

        postProcessor.doAdditionalProcessing(kotlinFile, rangeMarker)
    }
}
