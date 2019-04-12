/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.compiler.configuration

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.cli.common.arguments.K2JSCompilerArguments
import org.jetbrains.kotlin.config.SettingConstants
import org.jetbrains.kotlin.config.SettingConstants.KOTLIN_TO_JS_COMPILER_ARGUMENTS_SECTION

@State(name = KOTLIN_TO_JS_COMPILER_ARGUMENTS_SECTION, storages = [(Storage(SettingConstants.KOTLIN_COMPILER_SETTINGS_FILE))])
class Kotlin2JsCompilerArgumentsHolder(project: Project) : BaseKotlinCompilerSettings<K2JSCompilerArguments>(project) {
    override fun createSettings() = K2JSCompilerArguments()

    override fun validateNewSettings(settings: K2JSCompilerArguments) {
        validateInheritedFieldsUnchanged(settings)
    }

    companion object {
        fun getInstance(project: Project) = ServiceManager.getService(project, Kotlin2JsCompilerArgumentsHolder::class.java)!!

    }
}
