/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.tasks

import org.gradle.api.Task
import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.utils.outputsCompatible

internal fun isBuildCacheSupported(): Boolean =
    gradleVersion >= GradleVersion.version("4.3")

internal fun isWorkerAPISupported(): Boolean =
    gradleVersion >= GradleVersion.version("4.3")

internal fun isBuildCacheEnabledForKotlin(): Boolean =
    isBuildCacheSupported() &&
            System.getProperty(KOTLIN_CACHING_ENABLED_PROPERTY)?.toBoolean() ?: true

internal fun <T : Task> T.cacheOnlyIfEnabledForKotlin() {
    // The `cacheIf` method may be missing if the Gradle version is too low:
    try {
        outputsCompatible.cacheIf { isBuildCacheEnabledForKotlin() }
    } catch (_: NoSuchMethodError) {
    }
}

private val gradleVersion = GradleVersion.current()

private const val KOTLIN_CACHING_ENABLED_PROPERTY = "kotlin.caching.enabled"
