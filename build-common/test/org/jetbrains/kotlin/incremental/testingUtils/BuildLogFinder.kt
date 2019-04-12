/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental.testingUtils

import java.io.File

data class BuildLogFinder(
        private val isDataContainerBuildLogEnabled: Boolean = false,
        private val isGradleEnabled: Boolean = false,
        private val isJsEnabled: Boolean = false
) {
    companion object {
        private const val JS_LOG = "js-build.log"
        private const val GRADLE_LOG = "gradle-build.log"
        private const val DATA_CONTAINER_LOG = "data-container-version-build.log"
        const val JS_JPS_LOG = "js-jps-build.log"
        private const val SIMPLE_LOG = "build.log"

        fun isJpsLogFile(file: File): Boolean =
            file.name in arrayOf(SIMPLE_LOG, JS_JPS_LOG, DATA_CONTAINER_LOG)
    }

    fun findBuildLog(dir: File): File? {
        val names = dir.list() ?: arrayOf()
        val files = names.filter { File(dir, it).isFile }.toSet()
        val matchedName = when {
            isJsEnabled && JS_LOG in files -> JS_LOG
            isGradleEnabled && GRADLE_LOG in files -> GRADLE_LOG
            isJsEnabled && JS_JPS_LOG in files -> JS_JPS_LOG
            isDataContainerBuildLogEnabled && DATA_CONTAINER_LOG in files -> DATA_CONTAINER_LOG
            SIMPLE_LOG in files -> SIMPLE_LOG
            else -> null
        }

        return File(dir, matchedName ?: return null)
    }
}

