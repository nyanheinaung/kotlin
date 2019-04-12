/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.maven.configuration

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.psi.PsiFile
import org.jetbrains.idea.maven.model.MavenConstants
import org.jetbrains.kotlin.idea.configuration.AbstractConfigureProjectByChangingFileTest
import org.jetbrains.kotlin.idea.configuration.NotificationMessageCollector
import org.jetbrains.kotlin.idea.test.PluginTestCaseBase
import org.jetbrains.kotlin.test.KotlinTestUtils
import org.jetbrains.kotlin.test.KotlinTestUtils.isAllFilesPresentTest
import java.io.File

abstract class AbstractMavenConfigureProjectByChangingFileTest : AbstractConfigureProjectByChangingFileTest<KotlinMavenConfigurator>() {
    fun doTestWithMaven(path: String) {
        val pathWithFile = path + "/" + MavenConstants.POM_XML
        doTest(pathWithFile, pathWithFile.replace("pom", "pom_after"), KotlinJavaMavenConfigurator())
    }

    fun doTestWithJSMaven(path: String) {
        val pathWithFile = path + "/" + MavenConstants.POM_XML
        doTest(pathWithFile, pathWithFile.replace("pom", "pom_after"), KotlinJavascriptMavenConfigurator())
    }

    override fun runConfigurator(
        module: Module,
        file: PsiFile,
        configurator: KotlinMavenConfigurator,
        version: String,
        collector: NotificationMessageCollector
    ) {
        WriteCommandAction.runWriteCommandAction(module.project) {
            configurator.configureModule(module, file, version, collector)
        }
    }

    override fun getProjectJDK(): Sdk {
        if (!isAllFilesPresentTest(getTestName(false))) {
            val root = KotlinTestUtils.getTestsRoot(this::class.java)
            val dir = KotlinTestUtils.getTestDataFileName(this::class.java, name)

            val pomFile = File("$root/$dir", MavenConstants.POM_XML)
            if (pomFile.readText().contains("<target>9</target>")) {
                return PluginTestCaseBase.mockJdk9()
            }
        }

        return super.getProjectJDK()
    }
}
