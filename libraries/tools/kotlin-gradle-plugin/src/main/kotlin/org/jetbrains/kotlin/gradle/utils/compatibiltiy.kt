/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.utils

import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.tasks.TaskInputs
import org.gradle.api.tasks.TaskOutputs
import org.gradle.util.GradleVersion

internal val Task.inputsCompatible: TaskInputs get() = inputs

internal val Task.outputsCompatible: TaskOutputs get() = outputs

private val propertyMethod by lazy {
    TaskInputs::class.java.methods.first {
        it.name == "property" && it.parameterTypes.contentEquals(arrayOf(String::class.java, Any::class.java))
    }
}

internal fun TaskInputs.propertyCompatible(name: String, value: Any) {
    propertyMethod(this, name, value)
}

private val inputsDirMethod by lazy {
    TaskInputs::class.java.methods.first {
        it.name == "dir" && it.parameterTypes.contentEquals(arrayOf(Any::class.java))
    }
}

internal fun TaskInputs.dirCompatible(dirPath: Any) {
    inputsDirMethod(this, dirPath)
}

internal fun checkGradleCompatibility(minSupportedVersion: GradleVersion = GradleVersion.version("4.1")) {
    val currentVersion = GradleVersion.current()
    if (currentVersion < minSupportedVersion) {
        throw GradleException(
            "Current version of Gradle $currentVersion is not compatible with Kotlin plugin. " +
                    "Please use Gradle $minSupportedVersion or newer or previous version of Kotlin plugin."
        )
    }
}