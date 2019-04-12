/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.backend.common.CodegenUtil
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

data class SourceInfo(val source: String, val pathOrCleanFQN: String, val linesInFile: Int) {

    companion object {
        fun createInfo(element: KtElement?, internalClassName: String): SourceInfo {
            assert(element != null) { "Couldn't create source mapper for null element $internalClassName" }
            val lineNumbers = CodegenUtil.getLineNumberForElement(element!!.containingFile, true)
                    ?: error("Couldn't extract line count in ${element.containingFile}")

            //TODO hack condition for package parts cleaning
            val isTopLevel = element is KtFile || (element is KtNamedFunction && element.getParent() is KtFile)
            val cleanedClassFqName = if (!isTopLevel) internalClassName else internalClassName.substringBefore('$')

            return SourceInfo(element.containingKtFile.name, cleanedClassFqName, lineNumbers)
        }

        fun createInfoForIr(lineNumbers: Int, internalClassName: String, containingFileName: String): SourceInfo {
            //TODO cut topLevel names
//            val isTopLevel = element is KtFile || (element is KtNamedFunction && element.getParent() is KtFile)
//            val cleanedClassFqName = if (!isTopLevel) internalClassName else internalClassName.substringBefore('$')

            return SourceInfo(containingFileName, internalClassName, lineNumbers)
        }
    }
}

