/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm.modules

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.name.FqName

interface JavaModuleResolver {
    fun checkAccessibility(fileFromOurModule: VirtualFile?, referencedFile: VirtualFile, referencedPackage: FqName?): AccessError?

    sealed class AccessError {
        object ModuleDoesNotReadUnnamedModule : AccessError()
        data class ModuleDoesNotReadModule(val dependencyModuleName: String) : AccessError()
        data class ModuleDoesNotExportPackage(val dependencyModuleName: String) : AccessError()
    }

    companion object SERVICE {
        fun getInstance(project: Project): JavaModuleResolver =
                ServiceManager.getService(project, JavaModuleResolver::class.java)
    }
}
