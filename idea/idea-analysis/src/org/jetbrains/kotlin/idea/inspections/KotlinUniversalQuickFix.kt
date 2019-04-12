/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.inspections

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project

interface KotlinUniversalQuickFix : IntentionAction, LocalQuickFix {
    override fun getName() = text

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        invoke(project, null, descriptor.psiElement?.containingFile)
    }
}