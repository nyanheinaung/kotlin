/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.run

import com.intellij.execution.CommonJavaRunConfigurationParameters
import com.intellij.execution.ExternalizablePath
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.JavaRunConfigurationModule
import com.intellij.execution.configurations.RefactoringListenerProvider

@Suppress("PropertyName", "MemberVisibilityCanBePrivate")
@Deprecated("Will be dropped in 1.2.20. Use KotlinRunConfiguration instead.")
abstract class JetRunConfiguration(
    name: String,
    runConfigurationModule: JavaRunConfigurationModule,
    factory: ConfigurationFactory
) :
    ModuleBasedConfigurationElement<JavaRunConfigurationModule>(name, runConfigurationModule, factory),
    CommonJavaRunConfigurationParameters,
    RefactoringListenerProvider {

    @JvmField
    var MAIN_CLASS_NAME: String? = null

    @JvmField
    var WORKING_DIRECTORY: String? = null

    override fun setWorkingDirectory(value: String?) {
        WORKING_DIRECTORY = ExternalizablePath.urlValue(value)
    }

    override fun getWorkingDirectory(): String? {
        return ExternalizablePath.localPathValue(WORKING_DIRECTORY)
    }
}