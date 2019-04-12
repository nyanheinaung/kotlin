/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jps.incremental.storages

import com.intellij.openapi.util.io.FileUtil

class PathFunctionPair(
    val path: String,
    val function: String
) : Comparable<PathFunctionPair> {
    override fun compareTo(other: PathFunctionPair): Int {
        val pathComp = FileUtil.comparePaths(path, other.path)

        if (pathComp != 0) return pathComp

        return function.compareTo(other.function)
    }

    override fun equals(other: Any?): Boolean =
        when (other) {
            is PathFunctionPair ->
                FileUtil.pathsEqual(path, other.path) && function == other.function
            else ->
                false
        }

    override fun hashCode(): Int = 31 * FileUtil.pathHashCode(path) + function.hashCode()
}
