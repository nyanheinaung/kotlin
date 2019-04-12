/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.util.application

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressIndicatorProvider
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project

fun <T> runReadAction(action: () -> T): T {
    return ApplicationManager.getApplication().runReadAction<T>(action)
}

fun <T> runWriteAction(action: () -> T): T {
    return ApplicationManager.getApplication().runWriteAction<T>(action)
}

fun Project.executeWriteCommand(name: String, command: () -> Unit) {
    CommandProcessor.getInstance().executeCommand(this, { runWriteAction(command) }, name, null)
}

fun <T> Project.executeWriteCommand(name: String, groupId: Any? = null, command: () -> T): T {
    return executeCommand<T>(name, groupId) { runWriteAction(command) }
}

fun <T> Project.executeCommand(name: String, groupId: Any? = null, command: () -> T): T {
    var result: T = null as T
    CommandProcessor.getInstance().executeCommand(this, { result = command() }, name, groupId)
    @Suppress("USELESS_CAST")
    return result as T
}

/**
 * ProgressManager.getProgressIndicator() is nullable in 181 and dynamic again in 182
 * BUNCH: 181
 */
@Suppress("IncompatibleAPI")
val ProgressManager.progressIndicatorNullable: ProgressIndicator? get() = progressIndicator