/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.script.experimental.dependencies

import java.io.File

data class ScriptDependencies(
        val javaHome: File? = null,
        val classpath: List<File> = emptyList(),
        val imports: List<String> = emptyList(),
        val sources: List<File> = emptyList(),
        val scripts: List<File> = emptyList()
) {
    companion object {
        val Empty = ScriptDependencies()
    }
}