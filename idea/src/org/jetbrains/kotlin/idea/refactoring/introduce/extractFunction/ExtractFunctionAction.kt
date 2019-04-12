/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.refactoring.introduce.extractFunction

import com.intellij.refactoring.RefactoringActionHandler
import com.intellij.lang.refactoring.RefactoringSupportProvider
import org.jetbrains.kotlin.idea.refactoring.KotlinRefactoringSupportProvider
import org.jetbrains.kotlin.idea.refactoring.introduce.*

class ExtractFunctionAction: AbstractIntroduceAction() {
    override fun getRefactoringHandler(provider: RefactoringSupportProvider): RefactoringActionHandler? =
            (provider as? KotlinRefactoringSupportProvider)?.getExtractFunctionHandler()
}

class ExtractFunctionToScopeAction: AbstractIntroduceAction() {
    override fun getRefactoringHandler(provider: RefactoringSupportProvider): RefactoringActionHandler? =
            (provider as? KotlinRefactoringSupportProvider)?.getExtractFunctionToScopeHandler()
}
