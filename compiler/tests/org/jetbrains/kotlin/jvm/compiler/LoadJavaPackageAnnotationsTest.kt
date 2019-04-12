/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jvm.compiler

import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.config.addJavaSourceRoots
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.config.*
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.load.java.lazy.descriptors.LazyJavaPackageFragment
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.lazy.JvmResolveUtil
import org.jetbrains.kotlin.test.ConfigurationKind
import org.jetbrains.kotlin.test.KotlinTestUtils
import org.jetbrains.kotlin.test.MockLibraryUtil
import org.jetbrains.kotlin.test.TestJdkKind
import org.jetbrains.kotlin.test.testFramework.KtUsefulTestCase
import org.jetbrains.kotlin.utils.Jsr305State
import java.io.File

class LoadJavaPackageAnnotationsTest : KtUsefulTestCase() {
    companion object {
        private val TEST_DATA_PATH = "compiler/testData/loadJavaPackageAnnotations/"
    }

    private fun doTest(useJavac: Boolean, configurator: (CompilerConfiguration) -> Unit) {
        val configuration = KotlinTestUtils.newConfiguration(
                ConfigurationKind.ALL, TestJdkKind.FULL_JDK, KotlinTestUtils.getAnnotationsJar()
        ).apply {
            if (useJavac) {
                put(JVMConfigurationKeys.USE_JAVAC, true)
            }
            languageVersionSettings = LanguageVersionSettingsImpl(
                    LanguageVersion.LATEST_STABLE, ApiVersion.LATEST_STABLE, mapOf(JvmAnalysisFlags.jsr305 to Jsr305State.STRICT)
            )
            configurator(this)
        }
        val environment =
                KotlinCoreEnvironment.createForTests(
                        myTestRootDisposable,
                        configuration,
                        EnvironmentConfigFiles.JVM_CONFIG_FILES
                ).apply {
                    if (useJavac) {
                        registerJavac()
                    }
                }
        val moduleDescriptor = JvmResolveUtil.analyze(environment).moduleDescriptor

        val packageFragmentDescriptor =
                moduleDescriptor.getPackage(FqName("test")).fragments
                        .singleOrNull {
                            it.getMemberScope().getContributedClassifier(Name.identifier("A"), NoLookupLocation.FROM_TEST) != null
                        }.let { assertInstanceOf(it, LazyJavaPackageFragment::class.java) }

        val annotation = packageFragmentDescriptor.annotations.findAnnotation(FqName("test.Ann"))
        assertNotNull(annotation)

        val singleAnnotation = packageFragmentDescriptor.annotations.singleOrNull()
        assertNotNull(singleAnnotation)

        assertEquals(FqName("test.Ann"), singleAnnotation!!.fqName)
    }

    fun testAnnotationFromSource() {
        doTest(useJavac = false) {
            it.addJavaSourceRoots(listOf(File(TEST_DATA_PATH)))
        }
    }

    fun testAnnotationFromSourceWithJavac() {
        doTest(useJavac = true) {
            it.addJavaSourceRoots(listOf(File(TEST_DATA_PATH)))
        }
    }

    fun testAnnotationFromCompiledCode() {
        val jar = prepareJar()

        doTest(useJavac = false) {
            it.addJvmClasspathRoot(jar)
        }
    }

    fun testAnnotationFromCompiledCodeWithJavac() {
        val jar = prepareJar()

        doTest(useJavac = true) {
            it.addJvmClasspathRoot(jar)
        }
    }

    private fun prepareJar() =
            MockLibraryUtil.compileJavaFilesLibraryToJar(TEST_DATA_PATH, "result.jar")
}
