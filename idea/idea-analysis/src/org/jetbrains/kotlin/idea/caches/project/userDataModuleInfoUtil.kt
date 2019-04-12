/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.caches.project

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement


// NO-OP implementation, full implementation only for AS3.3, AS3.4

fun collectModuleInfoByUserData(
    project: Project,
    virtualFile: VirtualFile
): List<IdeaModuleInfo> = emptyList()

fun collectModuleInfoByUserData(
    psiElement: PsiElement
): List<IdeaModuleInfo> = emptyList()
