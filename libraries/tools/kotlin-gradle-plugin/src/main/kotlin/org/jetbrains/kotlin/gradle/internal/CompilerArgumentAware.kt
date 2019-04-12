/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.internal

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.jetbrains.kotlin.cli.common.arguments.CommonToolArguments
import org.jetbrains.kotlin.compilerRunner.ArgumentUtils

interface CompilerArgumentAware<T : CommonToolArguments> {
    val serializedCompilerArguments: List<String>
        get() = ArgumentUtils.convertArgumentsToStringList(prepareCompilerArguments())

    val serializedCompilerArgumentsIgnoreClasspathIssues: List<String>
        get() = ArgumentUtils.convertArgumentsToStringList(prepareCompilerArguments(ignoreClasspathResolutionErrors = true))

    val defaultSerializedCompilerArguments: List<String>
        get() = createCompilerArgs()
            .also { setupCompilerArgs(it, defaultsOnly = true) }
            .let(ArgumentUtils::convertArgumentsToStringList)

    val filteredArgumentsMap: Map<String, String>
        get() = CompilerArgumentsGradleInput.createInputsMap(prepareCompilerArguments())

    fun createCompilerArgs(): T
    fun setupCompilerArgs(args: T, defaultsOnly: Boolean = false, ignoreClasspathResolutionErrors: Boolean = false)
}

internal fun <T : CommonToolArguments> CompilerArgumentAware<T>.prepareCompilerArguments(ignoreClasspathResolutionErrors: Boolean = false) =
    createCompilerArgs().also { setupCompilerArgs(it, ignoreClasspathResolutionErrors = ignoreClasspathResolutionErrors) }

interface CompilerArgumentAwareWithInput<T : CommonToolArguments> : CompilerArgumentAware<T> {
    @get:Internal
    override val serializedCompilerArguments: List<String>
        get() = super.serializedCompilerArguments

    @get:Internal
    override val defaultSerializedCompilerArguments: List<String>
        get() = super.defaultSerializedCompilerArguments

    @get:Input
    override val filteredArgumentsMap: Map<String, String>
        get() = super.filteredArgumentsMap
}