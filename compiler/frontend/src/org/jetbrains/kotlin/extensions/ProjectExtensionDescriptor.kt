/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.extensions

import com.intellij.openapi.extensions.ExtensionPoint
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.project.Project

open class ProjectExtensionDescriptor<T>(name: String, private val extensionClass: Class<T>) {
    val extensionPointName: ExtensionPointName<T> = ExtensionPointName.create(name)

    fun registerExtensionPoint(project: Project) {
        Extensions.getArea(project).registerExtensionPoint(
            extensionPointName.name,
            extensionClass.name,
            ExtensionPoint.Kind.INTERFACE
        )
    }

    fun registerExtension(project: Project, extension: T) {
        Extensions.getArea(project).getExtensionPoint(extensionPointName).registerExtension(extension)
    }

    fun getInstances(project: Project): List<T> {
        val projectArea = Extensions.getArea(project)
        if (!projectArea.hasExtensionPoint(extensionPointName.name)) return listOf()

        return projectArea.getExtensionPoint(extensionPointName).extensions.toList()
    }
}