/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.actions

import com.intellij.ide.actions.ShowSettingsUtilImpl
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAwareAction
import org.jetbrains.kotlin.idea.configuration.KotlinUpdatesSettingsConfigurable

class ConfigurePluginUpdatesAction : DumbAwareAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        ShowSettingsUtilImpl.showSettingsDialog(project, KotlinUpdatesSettingsConfigurable.ID, "")
    }

    companion object {
        val ACTION_ID = "KotlinConfigureUpdates"
    }
}
