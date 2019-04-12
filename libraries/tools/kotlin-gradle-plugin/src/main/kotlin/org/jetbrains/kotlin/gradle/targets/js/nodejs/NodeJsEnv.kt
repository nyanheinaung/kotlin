/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.targets.js.nodejs

import java.io.*

internal data class NodeJsEnv(
        val nodeDir: File,
        val nodeBinDir: File,
        val nodeExec: String,
        val npmExec: String,

        val platformName: String,
        val architectureName: String,
        val ivyDependency: String
) {
    val isWindows: Boolean
        get() = platformName == "win"
}
