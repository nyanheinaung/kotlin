/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.refactoring.cutPaste

import com.intellij.codeInsight.editorActions.TextBlockTransferableData
import org.jetbrains.kotlin.idea.util.IdeDescriptorRenderers
import java.awt.datatransfer.DataFlavor

class MoveDeclarationsTransferableData(
        val sourceFileUrl: String,
        val sourceObjectFqName: String?,
        val stubTexts: List<String>,
        val declarationNames: Set<String>
) : TextBlockTransferableData {

    override fun getFlavor() = DATA_FLAVOR
    override fun getOffsetCount() = 0

    override fun getOffsets(offsets: IntArray?, index: Int) = index
    override fun setOffsets(offsets: IntArray?, index: Int) = index

    companion object {
        val DATA_FLAVOR = DataFlavor(MoveDeclarationsCopyPasteProcessor::class.java, "class: MoveDeclarationsCopyPasteProcessor")

        val STUB_RENDERER = IdeDescriptorRenderers.SOURCE_CODE.withOptions {
            defaultParameterValueRenderer = { "xxx" } // we need default value to be parsed as expression
        }
    }
}