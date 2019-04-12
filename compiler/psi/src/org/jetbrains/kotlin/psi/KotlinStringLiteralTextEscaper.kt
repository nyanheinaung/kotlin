/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.LiteralTextEscaper
import gnu.trove.TIntArrayList
import org.jetbrains.kotlin.psi.psiUtil.getContentRange
import org.jetbrains.kotlin.psi.psiUtil.isSingleQuoted

class KotlinStringLiteralTextEscaper(host: KtStringTemplateExpression) : LiteralTextEscaper<KtStringTemplateExpression>(host) {
    private var sourceOffsets: IntArray? = null

    override fun decode(rangeInsideHost: TextRange, outChars: StringBuilder): Boolean {
        val sourceOffsetsList = TIntArrayList()
        var sourceOffset = 0

        for (child in myHost.entries) {
            val childRange = TextRange.from(child.startOffsetInParent, child.textLength)
            if (rangeInsideHost.endOffset <= childRange.startOffset) {
                break
            }
            if (childRange.endOffset <= rangeInsideHost.startOffset) {
                continue
            }
            when (child) {
                is KtEscapeStringTemplateEntry -> {
                    if (!rangeInsideHost.contains(childRange)) {
                        //don't allow injection if its range starts or ends inside escaped sequence
                        //but still process offsets for the already decoded part
                        sourceOffsetsList.add(sourceOffset)
                        sourceOffsets = sourceOffsetsList.toNativeArray()
                        return false
                    }
                    val unescaped = child.unescapedValue
                    outChars.append(unescaped)
                    repeat(unescaped.length) {
                        sourceOffsetsList.add(sourceOffset)
                    }
                    sourceOffset += child.getTextLength()
                }
                else -> {
                    val textRange = rangeInsideHost.intersection(childRange)!!.shiftRight(-childRange.startOffset)
                    outChars.append(child.text, textRange.startOffset, textRange.endOffset)
                    repeat(textRange.length) {
                        sourceOffsetsList.add(sourceOffset++)
                    }
                }
            }
        }
        sourceOffsetsList.add(sourceOffset)
        sourceOffsets = sourceOffsetsList.toNativeArray()
        return true
    }

    override fun getOffsetInHost(offsetInDecoded: Int, rangeInsideHost: TextRange): Int {
        val offsets = sourceOffsets
        if (offsets == null || offsetInDecoded >= offsets.size) return -1
        return Math.min(offsets[offsetInDecoded], rangeInsideHost.length) + rangeInsideHost.startOffset
    }

    override fun getRelevantTextRange(): TextRange {
        return myHost.getContentRange()
    }

    override fun isOneLine(): Boolean {
        return myHost.isSingleQuoted()
    }
}
