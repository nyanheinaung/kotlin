/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.synthetic.res

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.analyzer.ModuleInfo

class CliAndroidPackageFragmentProviderExtension(private val isExperimental: Boolean) : AndroidPackageFragmentProviderExtension() {
    override fun isExperimental(moduleInfo: ModuleInfo?): Boolean = isExperimental

    override fun getLayoutXmlFileManager(project: Project, moduleInfo: ModuleInfo?): AndroidLayoutXmlFileManager? {
        return ServiceManager.getService(project, AndroidLayoutXmlFileManager::class.java)
    }
}