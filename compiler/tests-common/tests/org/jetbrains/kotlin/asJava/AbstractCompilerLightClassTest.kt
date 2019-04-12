/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.asJava

import com.intellij.psi.PsiClass
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.asJava.finder.JavaElementFinder
import org.jetbrains.kotlin.checkers.KotlinMultiFileTestWithJava
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.test.ConfigurationKind
import org.jetbrains.kotlin.test.InTextDirectivesUtils
import org.jetbrains.kotlin.test.KotlinTestUtils
import org.jetbrains.kotlin.util.KotlinFrontEndException
import org.junit.Assert
import java.io.File

abstract class AbstractCompilerLightClassTest : KotlinMultiFileTestWithJava<Void, Void>() {
    override fun getConfigurationKind(): ConfigurationKind = ConfigurationKind.ALL

    override fun isKotlinSourceRootNeeded(): Boolean = true

    override fun doMultiFileTest(file: File, modules: MutableMap<String, ModuleAndDependencies>, files: MutableList<Void>) {
        val environment = createEnvironment(file)
        val expectedFile = KotlinTestUtils.replaceExtension(file, "java")
        val allowFrontendExceptions = InTextDirectivesUtils.isDirectiveDefined(file.readText(), "// ALLOW_FRONTEND_EXCEPTION")

        LightClassTestCommon.testLightClass(
            expectedFile,
            file,
            { fqname -> findLightClass(allowFrontendExceptions, environment, fqname) },
            LightClassTestCommon::removeEmptyDefaultImpls
        )
    }

    override fun createTestModule(name: String): Void? = null

    override fun createTestFile(module: Void?, fileName: String, text: String, directives: Map<String, String>): Void? = null

    companion object {
        fun findLightClass(allowFrontendExceptions: Boolean, environment: KotlinCoreEnvironment, fqname: String): PsiClass? {
            assertException(allowFrontendExceptions, KotlinFrontEndException::class.java) {
                KotlinTestUtils.resolveAllKotlinFiles(environment)
            }

            val lightCLassForScript = KotlinAsJavaSupport
                .getInstance(environment.project)
                .getScriptClasses(FqName(fqname), GlobalSearchScope.allScope(environment.project))
                .firstOrNull()

            return lightCLassForScript ?: JavaElementFinder
                .getInstance(environment.project)
                .findClass(fqname, GlobalSearchScope.allScope(environment.project))
        }

        private fun assertException(shouldOccur: Boolean, klass: Class<out Throwable>, f: () -> Unit) {
            if (!shouldOccur) {
                f()
                return
            }

            var wasThrown = false
            try {
                f()
            } catch (e: Throwable) {
                wasThrown = true

                if (!shouldOccur || !klass.isAssignableFrom(e.javaClass)) {
                    throw e
                }
            } finally {
                if (shouldOccur && !wasThrown) {
                    Assert.fail("Expected exception wasn't thrown")
                }
            }
        }
    }
}
