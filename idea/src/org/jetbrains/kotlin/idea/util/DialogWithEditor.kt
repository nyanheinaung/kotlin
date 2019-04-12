/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.util

import com.intellij.openapi.ui.DialogWrapper

import javax.swing.*
import com.intellij.openapi.project.*
import com.intellij.openapi.editor.*
import java.awt.*
import com.intellij.testFramework.*
import org.jetbrains.kotlin.idea.*
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.editor.ex.*
import com.intellij.openapi.editor.colors.*
import java.awt.event.*
import com.intellij.psi.*

open class DialogWithEditor(
        val project: Project,
        title: String,
        val initialText: String
) : DialogWrapper(project, true) {
    val editor: Editor = createEditor()

    init {
        init()
        setTitle(title)
    }

    override final fun init() {
        super.init()
    }

    private fun createEditor(): Editor {
        val editorFactory = EditorFactory.getInstance()!!
        val virtualFile = LightVirtualFile("dummy.kt", KotlinFileType.INSTANCE, initialText)
        val document = FileDocumentManager.getInstance().getDocument(virtualFile)!!

        val editor = editorFactory.createEditor(document, project, KotlinFileType.INSTANCE, false)
        val settings = editor.settings
        settings.isVirtualSpace = false
        settings.isLineMarkerAreaShown = false
        settings.isFoldingOutlineShown = false
        settings.isRightMarginShown = false
        settings.isAdditionalPageAtBottom = false
        settings.additionalLinesCount = 2
        settings.additionalColumnsCount = 12

        assert(editor is EditorEx)
        (editor as EditorEx).isEmbeddedIntoDialogWrapper = true

        editor.getColorsScheme().setColor(EditorColors.CARET_ROW_COLOR, editor.getColorsScheme().defaultBackground)

        return editor
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(BorderLayout())
        panel.add(editor.component, BorderLayout.CENTER)
        return panel
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return editor.contentComponent
    }

    override fun dispose() {
        super.dispose()
        EditorFactory.getInstance()!!.releaseEditor(editor)
    }
}
