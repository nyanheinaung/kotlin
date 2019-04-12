/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.codeInsight.highlighting.actions.HighlightUsagesAction
import com.intellij.testFramework.ExpectedHighlightingData


abstract class AbstractAndroidUsageHighlightingTest : KotlinAndroidTestCase() {
    fun doTest(path: String) {
        copyResourceDirectoryForTest(path)
        val virtualFile = myFixture.copyFileToProject(path + getTestName(true) + ".kt", "src/" + getTestName(true) + ".kt")
        myFixture.configureFromExistingVirtualFile(virtualFile)

        val document = myFixture.editor.document
        val data = ExpectedHighlightingData(
                document,
                false,
                false,
                true,
                false,
                myFixture.file)

        data.init()

        myFixture.testAction(HighlightUsagesAction())

        val infos = myFixture.editor.markupModel.allHighlighters.map {
            HighlightInfo.newHighlightInfo(HighlightInfoType.INFORMATION).range(it.startOffset, it.endOffset).create()
        }

        data.checkResult(infos, document.text)
    }
}