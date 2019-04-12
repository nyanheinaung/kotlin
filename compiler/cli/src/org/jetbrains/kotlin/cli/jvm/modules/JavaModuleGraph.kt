/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.jvm.modules

import org.jetbrains.kotlin.resolve.jvm.modules.JavaModule
import org.jetbrains.kotlin.resolve.jvm.modules.JavaModuleFinder
import org.jetbrains.kotlin.storage.LockBasedStorageManager

class JavaModuleGraph(finder: JavaModuleFinder) {
    private val module: (String) -> JavaModule? =
        LockBasedStorageManager.NO_LOCKS.createMemoizedFunctionWithNullableValues(finder::findModule)

    fun getAllDependencies(moduleNames: List<String>): LinkedHashSet<String> {
        val visited = LinkedHashSet(moduleNames)

        // Every module implicitly depends on java.base
        visited += "java.base"

        fun dfs(moduleName: String) {
            // Automatic modules have no transitive exports, so we only consider explicit modules here
            val moduleInfo = (module(moduleName) as? JavaModule.Explicit)?.moduleInfo ?: return
            for ((dependencyModuleName, isTransitive) in moduleInfo.requires) {
                if (isTransitive && visited.add(dependencyModuleName)) {
                    dfs(dependencyModuleName)
                }
            }
        }

        for (moduleName in moduleNames) {
            val module = module(moduleName) ?: continue
            when (module) {
                is JavaModule.Automatic -> {
                    // Do nothing; all automatic modules should be added to compilation roots at call site as per java.lang.module javadoc
                }
                is JavaModule.Explicit -> {
                    for ((dependencyModuleName) in module.moduleInfo.requires) {
                        if (visited.add(dependencyModuleName)) {
                            dfs(dependencyModuleName)
                        }
                    }
                }
                else -> error("Unknown module: $module (${module.javaClass})")
            }
        }

        return visited
    }

    fun reads(moduleName: String, dependencyName: String): Boolean {
        if (moduleName == dependencyName || dependencyName == "java.base") return true

        val visited = linkedSetOf<String>()

        fun dfs(name: String): Boolean {
            if (!visited.add(name)) return false

            val module = module(name) ?: return false
            when (module) {
                is JavaModule.Automatic -> return true
                is JavaModule.Explicit -> {
                    for ((dependencyModuleName, isTransitive) in module.moduleInfo.requires) {
                        if (dependencyModuleName == dependencyName) return true
                        if (isTransitive && dfs(dependencyName)) return true
                    }
                    return false
                }
                else -> error("Unsupported module type: $module")
            }
        }

        val module = module(moduleName) ?: return false
        when (module) {
            is JavaModule.Automatic -> return true
            is JavaModule.Explicit -> {
                for ((dependencyModuleName) in module.moduleInfo.requires) {
                    if (dfs(dependencyModuleName)) return true
                }
            }
        }

        return dfs(moduleName)
    }
}
