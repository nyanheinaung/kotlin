/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.jvm.config

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.config.ContentRoot
import org.jetbrains.kotlin.cli.common.config.KotlinSourceRoot
import org.jetbrains.kotlin.config.CompilerConfiguration
import java.io.File

interface JvmContentRoot : ContentRoot {
    val file: File
}

data class JvmClasspathRoot(override val file: File) : JvmContentRoot

data class JavaSourceRoot(override val file: File, val packagePrefix: String?) : JvmContentRoot

data class JvmModulePathRoot(override val file: File) : JvmContentRoot

fun CompilerConfiguration.addJvmClasspathRoot(file: File) {
    add(CLIConfigurationKeys.CONTENT_ROOTS, JvmClasspathRoot(file))
}

fun CompilerConfiguration.addJvmClasspathRoots(files: List<File>) {
    files.forEach(this::addJvmClasspathRoot)
}

fun CompilerConfiguration.addJvmSdkRoots(files: List<File>) {
    addAll(CLIConfigurationKeys.CONTENT_ROOTS, 0, files.map(::JvmClasspathRoot))
}

val CompilerConfiguration.jvmClasspathRoots: List<File>
    get() = getList(CLIConfigurationKeys.CONTENT_ROOTS).filterIsInstance<JvmClasspathRoot>().map(JvmContentRoot::file)

@JvmOverloads
fun CompilerConfiguration.addJavaSourceRoot(file: File, packagePrefix: String? = null) {
    add(CLIConfigurationKeys.CONTENT_ROOTS, JavaSourceRoot(file, packagePrefix))
}

@JvmOverloads
fun CompilerConfiguration.addJavaSourceRoots(files: List<File>, packagePrefix: String? = null) {
    files.forEach { addJavaSourceRoot(it, packagePrefix) }
}

val CompilerConfiguration.javaSourceRoots: Set<String>
    get() = getList(CLIConfigurationKeys.CONTENT_ROOTS).mapNotNullTo(linkedSetOf()) { root ->
        when (root) {
            is KotlinSourceRoot -> root.path
            is JavaSourceRoot -> root.file.path
            else -> null
        }
    }
