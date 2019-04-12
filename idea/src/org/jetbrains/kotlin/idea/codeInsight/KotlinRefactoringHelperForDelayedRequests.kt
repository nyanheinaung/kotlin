/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.codeInsight

import com.intellij.refactoring.RefactoringHelper
import com.intellij.usageView.UsageInfo
import com.intellij.openapi.project.Project
import com.intellij.openapi.application.ApplicationManager
import org.jetbrains.kotlin.idea.core.ShortenReferences
import org.jetbrains.kotlin.idea.codeInsight.shorten.*
import org.jetbrains.kotlin.idea.util.application.runWriteAction

class KotlinRefactoringHelperForDelayedRequests : RefactoringHelper<Any> {
    override fun prepareOperation(usages: Array<out UsageInfo>?): Any? {
        if (usages != null && usages.isNotEmpty()) {
            val project = usages[0].project
            prepareDelayedRequests(project)
        }
        return null
    }

    override fun performOperation(project: Project, operationData: Any?) {
        runWriteAction { performDelayedRefactoringRequests(project) }
    }
}
