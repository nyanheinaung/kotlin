/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.tasks

import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.cli.common.arguments.K2JSDceArguments
import org.jetbrains.kotlin.cli.js.dce.K2JSDce
import org.jetbrains.kotlin.gradle.logging.GradleKotlinLogger
import org.jetbrains.kotlin.compilerRunner.runToolInSeparateProcess
import org.jetbrains.kotlin.gradle.dsl.KotlinJsDce
import org.jetbrains.kotlin.gradle.dsl.KotlinJsDceOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJsDceOptionsImpl
import java.io.File

@CacheableTask
 open class KotlinJsDce : AbstractKotlinCompileTool<K2JSDceArguments>(), KotlinJsDce {

    init {
        cacheOnlyIfEnabledForKotlin()
    }

    override fun localStateDirectories(): FileCollection = project.files()

    override fun createCompilerArgs(): K2JSDceArguments = K2JSDceArguments()

    override fun setupCompilerArgs(args: K2JSDceArguments, defaultsOnly: Boolean, ignoreClasspathResolutionErrors: Boolean) {
        dceOptionsImpl.updateArguments(args)
        args.declarationsToKeep = keep.toTypedArray()
    }

    private val dceOptionsImpl = KotlinJsDceOptionsImpl()

    @get:Internal
    override val dceOptions: KotlinJsDceOptions
        get() = dceOptionsImpl

    @get:Input
    override val keep: MutableList<String> = mutableListOf()

    override fun findKotlinCompilerClasspath(project: Project): List<File> = findKotlinJsDceClasspath(project)

    override fun compile() {}

    override fun keep(vararg fqn: String) {
        keep += fqn
    }

    @TaskAction
    fun performDce() {
        val inputFiles = (listOf(getSource()) + classpath.map { project.fileTree(it) })
            .reduce(FileTree::plus)
            .files.map { it.path }

        val outputDirArgs = arrayOf("-output-dir", destinationDir.path)

        val argsArray = serializedCompilerArguments.toTypedArray()

        val log = GradleKotlinLogger(logger)
        val allArgs = argsArray + outputDirArgs + inputFiles
        val exitCode = runToolInSeparateProcess(
            allArgs, K2JSDce::class.java.name, computedCompilerClasspath,
            log
        )
        throwGradleExceptionIfError(exitCode)
    }
}