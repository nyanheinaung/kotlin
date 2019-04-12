/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.modules

interface Module {
    fun getModuleName(): String

    fun getModuleType(): String

    fun getOutputDirectory(): String

    fun getFriendPaths(): List<String>

    fun getSourceFiles(): List<String>

    fun getCommonSourceFiles(): List<String>

    fun getClasspathRoots(): List<String>

    fun getJavaSourceRoots(): List<JavaRootPath>

    val modularJdkRoot: String?
}

data class JavaRootPath(val path: String, val packagePrefix: String? = null)
