/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import java.io.File

open class SubpluginOption(val key: String, private val lazyValue: Lazy<String>) {
    constructor(key: String, value: String) : this(key, lazyOf(value))

    val value: String get() = lazyValue.value
}

class FilesSubpluginOption(
    key: String,
    val files: Iterable<File>,
    val kind: FilesOptionKind = FilesOptionKind.INTERNAL,
    lazyValue: Lazy<String> = lazy { files.joinToString(File.pathSeparator) { it.canonicalPath } }
) : SubpluginOption(key, lazyValue) {

    constructor(
        key: String,
        files: List<File>,
        kind: FilesOptionKind = FilesOptionKind.INTERNAL,
        value: String? = null
    ) : this(key, files, kind, lazy { value ?: files.joinToString(File.pathSeparator) { it.canonicalPath } })
}

class CompositeSubpluginOption(
    key: String,
    lazyValue: Lazy<String>,
    val originalOptions: List<SubpluginOption>
) : SubpluginOption(key, lazyValue) {
    constructor(key: String, value: String, originalOptions: List<SubpluginOption>) : this(key, lazyOf(value), originalOptions)
}

/** Defines how the files option should be handled with regard to Gradle model */
enum class FilesOptionKind {
    /** The files option is an implementation detail and should not be treated as an input or an output.  */
    INTERNAL

    // More options might be added when use cases appear for them,
    // such as output directories, inputs or classpath options.
}

/** Defines a subplugin option that should be excluded from Gradle input/output checks */
open class InternalSubpluginOption(key: String, value: String) : SubpluginOption(key, value)

interface KotlinGradleSubplugin<in KotlinCompile : AbstractCompile> {
    fun isApplicable(project: Project, task: AbstractCompile): Boolean

    fun apply(
        project: Project,
        kotlinCompile: KotlinCompile,
        javaCompile: AbstractCompile?,
        variantData: Any?,
        androidProjectHandler: Any?,
        kotlinCompilation: KotlinCompilation<KotlinCommonOptions>?
    ): List<SubpluginOption>

    fun getSubpluginKotlinTasks(
            project: Project,
            kotlinCompile: KotlinCompile
    ): List<AbstractCompile> = emptyList()

    fun getCompilerPluginId(): String

    fun getPluginArtifact(): SubpluginArtifact
    fun getNativeCompilerPluginArtifact(): SubpluginArtifact? = null
}

open class SubpluginArtifact(val groupId: String, val artifactId: String, val version: String? = null)

class JetBrainsSubpluginArtifact(artifactId: String) : SubpluginArtifact(groupId = "org.jetbrains.kotlin", artifactId = artifactId)
