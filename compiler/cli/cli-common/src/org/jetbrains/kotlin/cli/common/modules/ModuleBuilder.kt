/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.modules

import org.jetbrains.kotlin.modules.JavaRootPath
import org.jetbrains.kotlin.modules.Module
import java.util.*

class ModuleBuilder(
    private val name: String,
    private val outputDir: String,
    private val type: String
) : Module {
    private val sourceFiles = ArrayList<String>()
    private val commonSourceFiles = ArrayList<String>()
    private val classpathRoots = ArrayList<String>()
    private val javaSourceRoots = ArrayList<JavaRootPath>()
    private val friendDirs = ArrayList<String>()
    override var modularJdkRoot: String? = null

    fun addSourceFiles(path: String) {
        sourceFiles.add(path)
    }

    fun addCommonSourceFiles(path: String) {
        commonSourceFiles.add(path)
    }

    fun addClasspathEntry(path: String) {
        classpathRoots.add(path)
    }

    fun addJavaSourceRoot(rootPath: JavaRootPath) {
        javaSourceRoots.add(rootPath)
    }

    fun addFriendDir(friendDir: String) {
        friendDirs.add(friendDir)
    }

    override fun getOutputDirectory(): String = outputDir
    override fun getFriendPaths(): List<String> = friendDirs
    override fun getJavaSourceRoots(): List<JavaRootPath> = javaSourceRoots
    override fun getSourceFiles(): List<String> = sourceFiles
    override fun getCommonSourceFiles(): List<String> = commonSourceFiles
    override fun getClasspathRoots(): List<String> = classpathRoots
    override fun getModuleName(): String = name
    override fun getModuleType(): String = type

    override fun toString() = "$name ($type)"
}
