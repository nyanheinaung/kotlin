/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.kotlin

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.descriptors.ModuleDescriptor

interface VirtualFileFinderFactory : MetadataFinderFactory {
    override fun create(scope: GlobalSearchScope): VirtualFileFinder
    override fun create(project: Project, module: ModuleDescriptor): VirtualFileFinder

    companion object SERVICE {
        fun getInstance(project: Project): VirtualFileFinderFactory =
                ServiceManager.getService(project, VirtualFileFinderFactory::class.java)
    }
}
