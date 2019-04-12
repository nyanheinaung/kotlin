/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.structure.impl

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.SearchScope
import org.jetbrains.kotlin.load.java.structure.JavaClass

interface VirtualFileBoundJavaClass : JavaClass {
    val virtualFile: VirtualFile?

    fun isFromSourceCodeInScope(scope: SearchScope): Boolean
}