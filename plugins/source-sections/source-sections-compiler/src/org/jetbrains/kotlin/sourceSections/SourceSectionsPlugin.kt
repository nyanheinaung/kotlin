/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.sourceSections

import com.intellij.mock.MockProject
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.extensions.PreprocessedVirtualFileFactoryExtension

object SourceSectionsConfigurationKeys {
    val SECTIONS_OPTION: CompilerConfigurationKey<List<String>> =
            CompilerConfigurationKey.create("allowed section name")
}

class SourceSectionsCommandLineProcessor : CommandLineProcessor {
    companion object {
        val SECTIONS_OPTION = CliOption("allowedSection", "<name>", "Allowed section name",
                                          required = true, allowMultipleOccurrences = true)

        val PLUGIN_ID = "org.jetbrains.kotlin.sourceSections"
    }

    override val pluginId = PLUGIN_ID
    override val pluginOptions = listOf(SECTIONS_OPTION)

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) = when (option) {
        SECTIONS_OPTION -> {
            val paths = configuration.getList(SourceSectionsConfigurationKeys.SECTIONS_OPTION).toMutableList()
            paths.add(value)
            configuration.put(SourceSectionsConfigurationKeys.SECTIONS_OPTION, paths)
        }
        else -> throw CliOptionProcessingException("Unknown option: ${option.optionName}")
    }
}

class SourceSectionsComponentRegistrar : ComponentRegistrar {
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        val sections = configuration.get(SourceSectionsConfigurationKeys.SECTIONS_OPTION) ?: return
        registerAllowedSourceSections(project, sections)
    }
}

fun registerAllowedSourceSections(project: Project, sections: List<String>) {
    if (!sections.isEmpty()) {
        PreprocessedVirtualFileFactoryExtension.registerExtension(project, FilteredSectionsVirtualFileExtension(sections.toHashSet()))
    }
}

