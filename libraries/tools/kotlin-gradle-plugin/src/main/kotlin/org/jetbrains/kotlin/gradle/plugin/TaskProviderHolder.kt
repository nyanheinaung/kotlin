/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin

import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

class TaskProviderHolder<T : Task>(
    private val name: String,
    private val provider: TaskProvider<T>
) : TaskHolder<T> {

    override fun getTaskOrProvider(): Any = provider

    override fun doGetTask(): T = provider.get()

    override fun configure(action: (T) -> (Unit)) {
        provider.configure(action)
    }

    override fun toString(): String {
        return "TaskProviderHolder instance: [className: ${javaClass.name}, task name: '$name']"
    }
}
