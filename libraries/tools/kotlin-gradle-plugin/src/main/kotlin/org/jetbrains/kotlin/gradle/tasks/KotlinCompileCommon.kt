/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.tasks

import org.gradle.api.Project
import org.gradle.api.tasks.CacheableTask
import org.jetbrains.kotlin.cli.common.arguments.K2MetadataCompilerArguments
import org.jetbrains.kotlin.compilerRunner.GradleCompilerEnvironment
import org.jetbrains.kotlin.compilerRunner.OutputItemsCollectorImpl
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformCommonOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformCommonOptionsImpl
import org.jetbrains.kotlin.gradle.dsl.fillDefaultValues
import org.jetbrains.kotlin.gradle.internal.tasks.allOutputFiles
import org.jetbrains.kotlin.gradle.logging.GradlePrintingMessageCollector
import org.jetbrains.kotlin.incremental.ChangedFiles
import java.io.File

@CacheableTask
open class KotlinCompileCommon : AbstractKotlinCompile<K2MetadataCompilerArguments>(), KotlinCommonCompile {

    private val kotlinOptionsImpl = KotlinMultiplatformCommonOptionsImpl()
    override val kotlinOptions: KotlinMultiplatformCommonOptions
        get() = kotlinOptionsImpl

    override fun createCompilerArgs(): K2MetadataCompilerArguments =
        K2MetadataCompilerArguments()

    override fun getSourceRoots(): SourceRoots =
        SourceRoots.KotlinOnly.create(getSource(), sourceFilesExtensions)

    override fun findKotlinCompilerClasspath(project: Project): List<File> =
        findKotlinMetadataCompilerClasspath(project)

    override fun setupCompilerArgs(args: K2MetadataCompilerArguments, defaultsOnly: Boolean, ignoreClasspathResolutionErrors: Boolean) {
        args.apply { fillDefaultValues() }
        super.setupCompilerArgs(args, defaultsOnly = defaultsOnly, ignoreClasspathResolutionErrors = ignoreClasspathResolutionErrors)

        args.moduleName = friendTask?.moduleName ?: this@KotlinCompileCommon.moduleName

        if (defaultsOnly) return

        val classpathList = classpath.files.toMutableList()
        friendTask?.let { classpathList.add(it.destinationDir) }

        with(args) {
            classpath = classpathList.joinToString(File.pathSeparator)
            destination = destinationDir.canonicalPath
        }

        kotlinOptionsImpl.updateArguments(args)
    }

    override fun callCompilerAsync(args: K2MetadataCompilerArguments, sourceRoots: SourceRoots, changedFiles: ChangedFiles) {
        val messageCollector = GradlePrintingMessageCollector(logger)
        val outputItemCollector = OutputItemsCollectorImpl()
        val compilerRunner = compilerRunner()
        val environment = GradleCompilerEnvironment(
            computedCompilerClasspath, messageCollector, outputItemCollector,
            buildReportMode = buildReportMode,
            outputFiles = allOutputFiles()
        )
        compilerRunner.runMetadataCompilerAsync(sourceRoots.kotlinSourceFiles, args, environment)
    }
}