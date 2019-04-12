/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.configuration

import com.intellij.openapi.module.Module
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.test.PluginTestCaseBase
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.test.KotlinTestUtils
import org.jetbrains.kotlin.test.KotlinTestUtils.isAllFilesPresentTest
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile
import java.io.File

abstract class AbstractGradleConfigureProjectByChangingFileTest :
    AbstractConfigureProjectByChangingFileTest<KotlinWithGradleConfigurator>() {
    fun doTestGradle(path: String) {
        val (before, after) = beforeAfterFiles()
        doTest(before, after, if ("js" in path) KotlinJsGradleModuleConfigurator() else KotlinGradleModuleConfigurator())
    }

    private fun beforeAfterFiles(): Pair<String, String> {
        val root = KotlinTestUtils.getTestsRoot(this::class.java)
        val test = KotlinTestUtils.getTestDataFileName(this::class.java, name)
        val path = "$root/$test"
        val testFile = File(path)

        if (testFile.isFile) {
            return path to path.replace("before", "after")
        }

        return when {
            File(path, "build_before.gradle").exists() ->
                "$path/build_before.gradle" to "$path/build_after.gradle"

            File(path, "build_before.gradle.kts").exists() ->
                "$path/build_before.gradle.kts" to "$path/build_after.gradle.kts"

            else -> error("Can't find test data files")
        }
    }

    override fun runConfigurator(
        module: Module,
        file: PsiFile,
        configurator: KotlinWithGradleConfigurator,
        version: String,
        collector: NotificationMessageCollector
    ) {
        if (file !is GroovyFile && file !is KtFile) {
            fail("file $file is not a GroovyFile or KtFile")
            return
        }

        configurator.configureModule(module, file, true, version, collector, ArrayList())
        configurator.configureModule(module, file, false, version, collector, ArrayList())
    }

    override fun getProjectJDK(): Sdk {
        if (!isAllFilesPresentTest(getTestName(false))) {
            val beforeAfterFiles = beforeAfterFiles()
            val (before, _) = beforeAfterFiles
            val gradleFile = File(before)
            if (gradleFile.readText().contains("1.9")) {
                return PluginTestCaseBase.mockJdk9()
            }
        }

        return super.getProjectJDK()
    }
}
