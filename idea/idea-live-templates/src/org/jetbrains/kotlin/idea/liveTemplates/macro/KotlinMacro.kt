/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.liveTemplates.macro

import com.intellij.codeInsight.template.Macro
import com.intellij.codeInsight.template.TemplateContextType
import org.jetbrains.kotlin.idea.liveTemplates.KotlinTemplateContextType

abstract class KotlinMacro : Macro() {
    override fun isAcceptableInContext(context: TemplateContextType?): Boolean {
        return context is KotlinTemplateContextType
    }
}
