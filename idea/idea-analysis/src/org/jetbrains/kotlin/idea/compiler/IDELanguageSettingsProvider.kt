/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.compiler

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootModificationTracker
import com.intellij.openapi.util.Key
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.jetbrains.kotlin.analyzer.LanguageSettingsProvider
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.cli.common.arguments.Jsr305Parser
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.arguments.parseCommandLineArguments
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.KotlinFacetSettingsProvider
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.config.TargetPlatformVersion
import org.jetbrains.kotlin.idea.caches.project.*
import org.jetbrains.kotlin.idea.project.getLanguageVersionSettings
import org.jetbrains.kotlin.idea.project.languageVersionSettings
import org.jetbrains.kotlin.idea.project.platform
import org.jetbrains.kotlin.script.KotlinScriptDefinition
import org.jetbrains.kotlin.utils.Jsr305State

object IDELanguageSettingsProvider : LanguageSettingsProvider {
    override fun getLanguageVersionSettings(
        moduleInfo: ModuleInfo,
        project: Project,
        isReleaseCoroutines: Boolean?
    ): LanguageVersionSettings =
        when (moduleInfo) {
            is ModuleSourceInfo -> moduleInfo.module.languageVersionSettings
            is LibraryInfo -> project.getLanguageVersionSettings(
                jsr305State = computeJsr305State(project), isReleaseCoroutines = isReleaseCoroutines
            )
            is ScriptModuleInfo -> getLanguageSettingsForScripts(project, moduleInfo.scriptDefinition).languageVersionSettings
            is ScriptDependenciesInfo.ForFile -> getLanguageSettingsForScripts(project, moduleInfo.scriptDefinition).languageVersionSettings
            is PlatformModuleInfo -> moduleInfo.platformModule.module.languageVersionSettings
            else -> project.getLanguageVersionSettings()
        }

    private fun computeJsr305State(project: Project): Jsr305State? {
        var result: Jsr305State? = null
        for (module in ModuleManager.getInstance(project).modules) {
            val settings = KotlinFacetSettingsProvider.getInstance(project).getSettings(module) ?: continue
            val compilerArguments = settings.mergedCompilerArguments as? K2JVMCompilerArguments ?: continue

            result = Jsr305Parser(MessageCollector.NONE).parse(
                compilerArguments.jsr305,
                compilerArguments.supportCompatqualCheckerFrameworkAnnotations
            )

        }
        return result
    }

    override fun getTargetPlatform(moduleInfo: ModuleInfo, project: Project): TargetPlatformVersion =
        when (moduleInfo) {
            is ModuleSourceInfo -> moduleInfo.module.platform?.version ?: TargetPlatformVersion.NoVersion
            is ScriptModuleInfo -> getLanguageSettingsForScripts(project, moduleInfo.scriptDefinition).targetPlatformVersion
            is ScriptDependenciesInfo.ForFile -> getLanguageSettingsForScripts(project, moduleInfo.scriptDefinition).targetPlatformVersion
            else -> TargetPlatformVersion.NoVersion
        }
}

private data class ScriptLanguageSettings(
    val languageVersionSettings: LanguageVersionSettings,
    val targetPlatformVersion: TargetPlatformVersion
)

private val SCRIPT_LANGUAGE_SETTINGS = Key.create<CachedValue<ScriptLanguageSettings>>("SCRIPT_LANGUAGE_SETTINGS")

private fun getLanguageSettingsForScripts(project: Project, scriptDefinition: KotlinScriptDefinition): ScriptLanguageSettings {
    val args = scriptDefinition.additionalCompilerArguments
    return if (args == null || args.none()) {
        ScriptLanguageSettings(project.getLanguageVersionSettings(), TargetPlatformVersion.NoVersion)
    } else {
        val settings = scriptDefinition.getUserData(SCRIPT_LANGUAGE_SETTINGS) ?: createCachedValue(project) {
            val compilerArguments = K2JVMCompilerArguments()
            parseCommandLineArguments(args.toList(), compilerArguments)
            // TODO: reporting
            val verSettings = compilerArguments.toLanguageVersionSettings(MessageCollector.NONE)
            val jvmTarget = compilerArguments.jvmTarget?.let { JvmTarget.fromString(it) } ?: TargetPlatformVersion.NoVersion
            ScriptLanguageSettings(verSettings, jvmTarget)
        }.also { scriptDefinition.putUserData(SCRIPT_LANGUAGE_SETTINGS, it) }
        settings.value
    }
}

private fun createCachedValue(project: Project, body: () -> ScriptLanguageSettings): CachedValue<ScriptLanguageSettings> {
    return CachedValuesManager
        .getManager(project)
        .createCachedValue(
            {
                CachedValueProvider.Result(
                    body(),
                    ProjectRootModificationTracker.getInstance(project)
                )
            }, false
        )
}
