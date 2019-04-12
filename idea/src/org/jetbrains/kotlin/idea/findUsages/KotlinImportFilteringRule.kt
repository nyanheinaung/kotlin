/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.findUsages

import com.intellij.usages.Usage
import com.intellij.usages.rules.ImportFilteringRule
import com.intellij.usages.rules.PsiElementUsage
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType

class KotlinImportFilteringRule : ImportFilteringRule() {
    override fun isVisible(usage: Usage): Boolean {
        if (usage is PsiElementUsage) {
            return usage.element?.getNonStrictParentOfType<KtImportDirective>() == null
        }

        return true
    }
}
