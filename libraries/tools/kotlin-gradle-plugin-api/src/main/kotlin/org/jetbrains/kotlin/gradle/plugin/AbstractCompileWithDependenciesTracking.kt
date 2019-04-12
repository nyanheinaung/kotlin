/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin

import org.gradle.api.Task
import org.gradle.api.tasks.compile.AbstractCompile

abstract class AbstractCompileWithDependenciesTracking : AbstractCompile() {
    open fun isDependentTaskOutOfDate(task: Task): Boolean = false
}

