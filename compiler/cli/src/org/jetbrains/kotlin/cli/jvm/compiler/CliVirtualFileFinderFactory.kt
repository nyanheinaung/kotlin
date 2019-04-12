/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.jvm.compiler

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.cli.jvm.index.JvmDependenciesIndex
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.load.kotlin.VirtualFileFinder
import org.jetbrains.kotlin.load.kotlin.VirtualFileFinderFactory

// TODO: create different JvmDependenciesIndex instances for different sets of source roots to improve performance
class CliVirtualFileFinderFactory(private val index: JvmDependenciesIndex) : VirtualFileFinderFactory {
    override fun create(scope: GlobalSearchScope): VirtualFileFinder = CliVirtualFileFinder(index, scope)

    override fun create(project: Project, module: ModuleDescriptor): VirtualFileFinder =
        CliVirtualFileFinder(index, GlobalSearchScope.allScope(project))
}
