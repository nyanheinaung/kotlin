/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle

import org.junit.Test
import java.io.File

class CoroutinesIT : BaseGradleIT() {
    @Test
    fun testCoroutinesJvmDefault() {
        jvmProject.doTest("default", null)
    }

    @Test
    fun testCoroutinesJsDefault() {
        jsProject.doTest("default", null)
    }

    // todo: replace with project that actually uses coroutines after their syntax is finalized
    private val jvmProject: Project
        get() = Project("kotlinProject")

    private val jsProject: Project
        get() = Project("kotlin2JsProject")

    private fun Project.doTest(coroutineSupport: String, propertyFileName: String?) {
        if (propertyFileName != null) {
            setupWorkingDir()
            val propertyFile = File(projectDir, propertyFileName)
            val coroutinesProperty = "kotlin.coroutines=$coroutineSupport"
            propertyFile.writeText(coroutinesProperty)
        }

        build("build") {
            assertContains("args.coroutinesState=$coroutineSupport")
        }
    }
}