/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.test.env.kotlin

import com.intellij.mock.MockProject
import java.io.File

abstract class AbstractCoreEnvironment {
    abstract val project: MockProject

    open fun dispose() {
        // Do nothing
    }

    abstract fun addJavaSourceRoot(root: File)
    abstract fun addJar(root: File)
}