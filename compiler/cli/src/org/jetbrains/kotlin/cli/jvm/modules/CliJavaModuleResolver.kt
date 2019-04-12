/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.jvm.modules

import com.intellij.ide.highlighter.JavaClassFileType
import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.vfs.StandardFileSystems
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.jvm.modules.JavaModule
import org.jetbrains.kotlin.resolve.jvm.modules.JavaModuleResolver

class CliJavaModuleResolver(
    private val moduleGraph: JavaModuleGraph,
    private val userModules: List<JavaModule>,
    private val systemModules: List<JavaModule.Explicit>
) : JavaModuleResolver {
    init {
        assert(userModules.count(JavaModule::isSourceModule) <= 1) {
            "Modules computed by ClasspathRootsResolver cannot have more than one source module: $userModules"
        }
    }

    private val sourceModule: JavaModule? = userModules.firstOrNull(JavaModule::isSourceModule)

    private fun findJavaModule(file: VirtualFile): JavaModule? {
        if (file.fileSystem.protocol == StandardFileSystems.JRT_PROTOCOL) {
            return systemModules.firstOrNull { module -> file in module }
        }

        return when (file.fileType) {
            KotlinFileType.INSTANCE, JavaFileType.INSTANCE -> sourceModule
            JavaClassFileType.INSTANCE -> userModules.firstOrNull { module -> file in module }
            else -> null
        }
    }

    private operator fun JavaModule.contains(file: VirtualFile): Boolean =
        moduleRoots.any { (root, isBinary) -> isBinary && VfsUtilCore.isAncestor(root, file, false) }

    override fun checkAccessibility(
        fileFromOurModule: VirtualFile?, referencedFile: VirtualFile, referencedPackage: FqName?
    ): JavaModuleResolver.AccessError? {
        val ourModule = fileFromOurModule?.let(this::findJavaModule)
        val theirModule = this.findJavaModule(referencedFile)

        if (ourModule?.name == theirModule?.name) return null

        if (theirModule == null) {
            return JavaModuleResolver.AccessError.ModuleDoesNotReadUnnamedModule
        }

        if (ourModule != null && !moduleGraph.reads(ourModule.name, theirModule.name)) {
            return JavaModuleResolver.AccessError.ModuleDoesNotReadModule(theirModule.name)
        }

        val fqName = referencedPackage ?: return null
        if (!theirModule.exports(fqName) && (ourModule == null || !theirModule.exportsTo(fqName, ourModule.name))) {
            return JavaModuleResolver.AccessError.ModuleDoesNotExportPackage(theirModule.name)
        }

        return null
    }
}
