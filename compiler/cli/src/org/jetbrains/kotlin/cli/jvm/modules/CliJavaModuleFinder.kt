/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.jvm.modules

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiJavaModule
import org.jetbrains.kotlin.resolve.jvm.modules.JavaModule
import org.jetbrains.kotlin.resolve.jvm.modules.JavaModuleFinder
import org.jetbrains.kotlin.resolve.jvm.modules.JavaModuleInfo

class CliJavaModuleFinder(jrtFileSystemRoot: VirtualFile?) : JavaModuleFinder {
    private val modulesRoot = jrtFileSystemRoot?.findChild("modules")
    private val userModules = linkedMapOf<String, JavaModule>()

    fun addUserModule(module: JavaModule) {
        userModules.putIfAbsent(module.name, module)
    }

    val allObservableModules: Sequence<JavaModule>
        get() = systemModules + userModules.values

    val systemModules: Sequence<JavaModule.Explicit>
        get() = modulesRoot?.children.orEmpty().asSequence().mapNotNull(this::findSystemModule)

    override fun findModule(name: String): JavaModule? =
        modulesRoot?.findChild(name)?.let(this::findSystemModule) ?: userModules[name]

    private fun findSystemModule(moduleRoot: VirtualFile): JavaModule.Explicit? {
        val file = moduleRoot.findChild(PsiJavaModule.MODULE_INFO_CLS_FILE) ?: return null
        val moduleInfo = JavaModuleInfo.read(file) ?: return null
        return JavaModule.Explicit(moduleInfo, listOf(JavaModule.Root(moduleRoot, isBinary = true)), file)
    }
}
