/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.refactoring.introduce.introduceProperty

import com.intellij.refactoring.RefactoringActionHandler
import com.intellij.lang.refactoring.RefactoringSupportProvider
import org.jetbrains.kotlin.idea.refactoring.KotlinRefactoringSupportProvider
import org.jetbrains.kotlin.idea.refactoring.introduce.*

class IntroducePropertyAction : AbstractIntroduceAction() {
    override fun getRefactoringHandler(provider: RefactoringSupportProvider): RefactoringActionHandler? =
            (provider as? KotlinRefactoringSupportProvider)?.getIntroducePropertyHandler()
}
