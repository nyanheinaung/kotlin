/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.console

import com.intellij.execution.ExecutionManager
import com.intellij.execution.Executor
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.compiler.CompilerManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.task.ProjectTaskManager

class ConsoleCompilerHelper(
    private val project: Project,
    private val module: Module,
    private val executor: Executor,
    private val contentDescriptor: RunContentDescriptor
) {

    fun moduleIsUpToDate(): Boolean {
        val compilerManager = CompilerManager.getInstance(project)
        val compilerScope = compilerManager.createModuleCompileScope(module, true)
        return compilerManager.isUpToDate(compilerScope)
    }

    fun compileModule() {
        if (ExecutionManager.getInstance(project).contentManager.removeRunContent(executor, contentDescriptor)) {
            ProjectTaskManager.getInstance(project).build(arrayOf(module)) { result ->
                if (!module.isDisposed) {
                    KotlinConsoleKeeper.getInstance(project).run(module, previousCompilationFailed = result.errors > 0)
                }
            }
        }
    }
}