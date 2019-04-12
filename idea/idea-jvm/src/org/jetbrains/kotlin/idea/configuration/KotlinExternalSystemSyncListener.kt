/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.configuration

import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskId
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskNotificationListenerAdapter
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskType
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.idea.configuration.ui.KotlinConfigurationCheckerComponent

class KotlinExternalSystemSyncListener : ExternalSystemTaskNotificationListenerAdapter() {
    override fun onStart(id: ExternalSystemTaskId, workingDir: String) {
        val project = id.findResolvedProject() ?: return
        KotlinMigrationProjectComponent.getInstanceIfNotDisposed(project)?.onImportAboutToStart()
        KotlinConfigurationCheckerComponent.getInstanceIfNotDisposed(project)?.syncStarted()
    }

    override fun onEnd(id: ExternalSystemTaskId) {
        // At this point changes might be still not applied to project structure yet.
        val project = id.findResolvedProject() ?: return
        KotlinConfigurationCheckerComponent.getInstanceIfNotDisposed(project)?.syncDone()
    }
}

internal fun ExternalSystemTaskId.findResolvedProject(): Project? {
    if (type != ExternalSystemTaskType.RESOLVE_PROJECT) return null
    return findProject()
}
