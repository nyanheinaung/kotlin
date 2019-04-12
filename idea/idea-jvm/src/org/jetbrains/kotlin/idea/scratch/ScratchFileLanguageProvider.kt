/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.scratch

import com.intellij.lang.Language
import com.intellij.lang.LanguageExtension
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.idea.scratch.output.ScratchOutputHandler

abstract class ScratchFileLanguageProvider {
    abstract fun createFile(project: Project, editor: TextEditor): ScratchFile?
    abstract fun createReplExecutor(file: ScratchFile): ScratchExecutor?
    abstract fun createCompilingExecutor(file: ScratchFile): ScratchExecutor?

    abstract fun getOutputHandler(): ScratchOutputHandler

    companion object {
        private val EXTENSION = LanguageExtension<ScratchFileLanguageProvider>("org.jetbrains.kotlin.scratchFileLanguageProvider")

        fun get(language: Language): ScratchFileLanguageProvider? {
            return ScratchFileLanguageProvider.EXTENSION.forLanguage(language)
        }

        fun get(fileType: FileType): ScratchFileLanguageProvider? {
            return (fileType as? LanguageFileType)?.language?.let { get(it) }
        }
    }
}