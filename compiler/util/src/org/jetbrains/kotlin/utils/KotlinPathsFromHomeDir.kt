/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.utils

import kotlin.collections.*

import java.io.File

class KotlinPathsFromHomeDir(
    override val homePath: File // kotlinc directory
) : KotlinPathsFromBaseDirectory(File(homePath, "lib")) {

    // TODO: extend when needed
    val libsWithSources = setOf(KotlinPaths.Jar.StdLib, KotlinPaths.Jar.JsStdLib)

    override fun sourcesJar(jar: KotlinPaths.Jar): File? = if (jar in libsWithSources) super.sourcesJar(jar) else null
}
