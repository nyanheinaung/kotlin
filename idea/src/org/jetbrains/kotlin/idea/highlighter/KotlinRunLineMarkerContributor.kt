/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.highlighter

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.isMainFunction
import org.jetbrains.kotlin.idea.platform.tooling
import org.jetbrains.kotlin.idea.project.platform
import org.jetbrains.kotlin.idea.util.module
import org.jetbrains.kotlin.psi.KtNamedFunction

class KotlinRunLineMarkerContributor : RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? {
        val function = element.parent as? KtNamedFunction ?: return null

        if (function.nameIdentifier != element) return null

        if (function.isMainFunction()) {
            val platform = function.containingKtFile.module?.platform ?: return null
            if (!platform.kind.tooling.acceptsAsEntryPoint(function)) return null

            return Info(AllIcons.RunConfigurations.TestState.Run, null, ExecutorAction.getActions(0))
        }

        return null
    }
}
