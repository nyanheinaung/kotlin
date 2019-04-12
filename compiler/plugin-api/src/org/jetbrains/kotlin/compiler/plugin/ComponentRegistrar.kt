/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.compiler.plugin

import com.intellij.mock.MockProject
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.config.CompilerConfiguration

interface ComponentRegistrar {
    companion object {
        val PLUGIN_COMPONENT_REGISTRARS: CompilerConfigurationKey<MutableList<ComponentRegistrar>> = CompilerConfigurationKey.create("plugin component registrars")
    }

    fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration)
}
