/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jps.build

import com.intellij.openapi.util.SystemInfoRt
import org.jetbrains.jps.model.java.JavaSourceRootType
import org.jetbrains.kotlin.incremental.testingUtils.Modification

class IncrementalProjectPathCaseChangedTest : AbstractIncrementalJpsTest(checkDumpsCaseInsensitively = true) {
    fun testProjectPathCaseChanged() {
        doTest("jps-plugin/testData/incremental/custom/projectPathCaseChanged/")
    }

    fun testProjectPathCaseChangedMultiFile() {
        doTest("jps-plugin/testData/incremental/custom/projectPathCaseChangedMultiFile/")
    }

    override fun doTest(testDataPath: String) {
        if (SystemInfoRt.isFileSystemCaseSensitive) {
            return
        }

        super.doTest(testDataPath)
    }

    override fun performAdditionalModifications(modifications: List<Modification>) {
        val module = myProject.modules[0]
        val sourceRoot = module.sourceRoots[0].url
        assert(sourceRoot.endsWith("/src"))
        val newSourceRoot = sourceRoot.replace("/src", "/SRC")
        module.removeSourceRoot(sourceRoot, JavaSourceRootType.SOURCE)
        module.addSourceRoot(newSourceRoot, JavaSourceRootType.SOURCE)
    }
}
