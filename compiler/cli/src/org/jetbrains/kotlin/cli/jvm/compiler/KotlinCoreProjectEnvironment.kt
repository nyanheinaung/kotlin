/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.jvm.compiler

import com.intellij.core.JavaCoreApplicationEnvironment
import com.intellij.core.JavaCoreProjectEnvironment
import com.intellij.openapi.Disposable
import com.intellij.psi.PsiManager

open class KotlinCoreProjectEnvironment(
    disposable: Disposable,
    applicationEnvironment: JavaCoreApplicationEnvironment
) : JavaCoreProjectEnvironment(disposable, applicationEnvironment) {
    override fun createCoreFileManager() = KotlinCliJavaFileManagerImpl(PsiManager.getInstance(project))
}
