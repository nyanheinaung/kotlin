/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.highlighter

import com.intellij.execution.TestStateStorage
import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.execution.testframework.TestIconMapper
import com.intellij.execution.testframework.sm.runner.states.TestStateInfo
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.platform.tooling
import org.jetbrains.kotlin.idea.project.platform
import org.jetbrains.kotlin.idea.util.projectStructure.module
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import javax.swing.Icon

class KotlinTestRunLineMarkerContributor : RunLineMarkerContributor() {
    companion object {
        fun getTestStateIcon(url: String, project: Project): Icon? {
            val defaultIcon = AllIcons.RunConfigurations.TestState.Run
            val state = TestStateStorage.getInstance(project).getState(url) ?: return defaultIcon
            return when (TestIconMapper.getMagnitude(state.magnitude)) {
                TestStateInfo.Magnitude.ERROR_INDEX,
                TestStateInfo.Magnitude.FAILED_INDEX -> AllIcons.RunConfigurations.TestState.Red2
                TestStateInfo.Magnitude.PASSED_INDEX,
                TestStateInfo.Magnitude.COMPLETE_INDEX -> AllIcons.RunConfigurations.TestState.Green2
                else -> defaultIcon
            }
        }
    }

    override fun getInfo(element: PsiElement): Info? {
        val declaration = element.getStrictParentOfType<KtNamedDeclaration>() ?: return null
        if (declaration.nameIdentifier != element) return null

        if (declaration !is KtClassOrObject && declaration !is KtNamedFunction) return null

        if (declaration is KtNamedFunction && declaration.containingClassOrObject == null) return null

        // To prevent IDEA failing on red code
        val descriptor = declaration.resolveToDescriptorIfAny() ?: return null

        val targetPlatform = declaration.module?.platform ?: return null
        val icon = targetPlatform.kind.tooling.getTestIcon(declaration, descriptor) ?: return null
        return Info(icon, { "Run Test" }, ExecutorAction.getActions())
    }
}
