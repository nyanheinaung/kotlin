/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.configure

import com.android.tools.idea.gradle.project.model.AndroidModuleModel
import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.Key
import com.intellij.openapi.externalSystem.model.ProjectKeys
import com.intellij.openapi.externalSystem.model.project.ProjectData
import com.intellij.openapi.externalSystem.service.project.IdeModifiableModelsProvider
import com.intellij.openapi.externalSystem.service.project.manage.AbstractProjectDataService
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.idea.configuration.GradleProjectImportHandler
import org.jetbrains.kotlin.idea.configuration.configureFacetByGradleModule

class KotlinGradleAndroidModuleModelProjectDataService : AbstractProjectDataService<AndroidModuleModel, Void>() {
    companion object {
        val KEY = Key<AndroidModuleModel>(AndroidModuleModel::class.qualifiedName!!, 0)
    }

    override fun getTargetDataKey() = KEY

    override fun postProcess(
        toImport: MutableCollection<DataNode<AndroidModuleModel>>,
        projectData: ProjectData?,
        project: Project,
        modelsProvider: IdeModifiableModelsProvider
    ) {
        super.postProcess(toImport, projectData, project, modelsProvider)
        for (moduleModelNode in toImport) {
            val moduleNode = ExternalSystemApiUtil.findParent(moduleModelNode, ProjectKeys.MODULE) ?: continue
            val moduleData = moduleNode.data
            val ideModule = modelsProvider.findIdeModule(moduleData) ?: continue
            val sourceSetName = moduleModelNode.data.selectedVariant.name
            val kotlinFacet = configureFacetByGradleModule(ideModule, modelsProvider, moduleNode, null, sourceSetName) ?: continue
            GradleProjectImportHandler.getInstances(project).forEach { it.importByModule(kotlinFacet, moduleNode) }
        }
    }
}