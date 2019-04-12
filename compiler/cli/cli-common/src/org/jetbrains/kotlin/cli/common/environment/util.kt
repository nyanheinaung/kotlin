/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.environment

import com.intellij.openapi.util.SystemInfo

fun setIdeaIoUseFallback() {
    if (SystemInfo.isWindows) {
        val properties = System.getProperties()

        properties.setProperty("idea.io.use.nio2", java.lang.Boolean.TRUE.toString())

        if (!(SystemInfo.isJavaVersionAtLeast("1.7") && "1.7.0-ea" != SystemInfo.JAVA_VERSION)) {
            properties.setProperty("idea.io.use.fallback", java.lang.Boolean.TRUE.toString())
        }
    }
}
