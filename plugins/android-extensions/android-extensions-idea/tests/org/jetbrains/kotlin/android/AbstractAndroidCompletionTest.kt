/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android

import com.intellij.codeInsight.CodeInsightSettings
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.openapi.util.io.FileUtil
import org.jetbrains.kotlin.idea.completion.test.testCompletion
import org.jetbrains.kotlin.resolve.jvm.platform.JvmPlatform
import java.io.File

abstract class AbstractAndroidCompletionTest : KotlinAndroidTestCase() {
    private var codeCompletionOldValue: Boolean = false
    private var smartTypeCompletionOldValue: Boolean = false

    override fun setUp() {
        super.setUp()

        val settings = CodeInsightSettings.getInstance()
        codeCompletionOldValue = settings.AUTOCOMPLETE_ON_CODE_COMPLETION
        smartTypeCompletionOldValue = settings.AUTOCOMPLETE_ON_SMART_TYPE_COMPLETION

        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (completionType()) {
            CompletionType.SMART -> settings.AUTOCOMPLETE_ON_SMART_TYPE_COMPLETION = false
            CompletionType.BASIC -> settings.AUTOCOMPLETE_ON_CODE_COMPLETION = false
        }
    }

    private fun completionType() = CompletionType.BASIC

    fun doTest(path: String) {
        copyResourceDirectoryForTest(path)
        val virtualFile = myFixture.copyFileToProject(path + getTestName(true) + ".kt", "src/" + getTestName(true) + ".kt")
        myFixture.configureFromExistingVirtualFile(virtualFile)
        val fileText = FileUtil.loadFile(File(path + getTestName(true) + ".kt"), true)
        testCompletion(fileText, JvmPlatform, { completionType, count -> myFixture.complete(completionType, count) })
    }

    override fun tearDown() {
        val settings = CodeInsightSettings.getInstance()
        settings.AUTOCOMPLETE_ON_CODE_COMPLETION = codeCompletionOldValue
        settings.AUTOCOMPLETE_ON_SMART_TYPE_COMPLETION = smartTypeCompletionOldValue

        super.tearDown()
    }
}
