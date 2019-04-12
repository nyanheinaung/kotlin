/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin

import org.gradle.api.Task
import org.gradle.api.Project


/**
 * Reference to a org.gradle.api.Task or  org.gradle.api.TaskProvider necessary in order to support flexible creation of tasks.
 * For gradle versions < 4.9 tasks are created meanwhile for gradle with version >= 4.9 tasks are registered
 */
interface TaskHolder<T : Task> {

    /**
     * Returns Task itself if task was created or TaskProvider<Task> if task was registered.
     */
    fun getTaskOrProvider(): Any

    /**
     * Returns instance of task. If task created using lazy api, it will be instantiated
     */
    fun doGetTask(): T


    /**
     * Invokes task configuration. If task was registered the configuration action is added but not invoked
     */
    fun configure(action: (T) -> (Unit))
}
