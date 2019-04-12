/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.tasks

import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class CompilerPluginOptions {
    internal val subpluginOptionsByPluginId =
        mutableMapOf<String, MutableList<SubpluginOption>>()

    val arguments: List<String>
        get() = subpluginOptionsByPluginId.flatMap { (pluginId, subplubinOptions) ->
            subplubinOptions.map { option ->
                "plugin:$pluginId:${option.key}=${option.value}"
            }
        }

    fun addPluginArgument(pluginId: String, option: SubpluginOption) {
        subpluginOptionsByPluginId.getOrPut(pluginId) { mutableListOf() }.add(option)
    }
}
