/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.compiler.configuration

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "KotlinCompilerWorkspaceSettings",
    storages = arrayOf(
        Storage(file = StoragePathMacros.WORKSPACE_FILE)
    )
)
class KotlinCompilerWorkspaceSettings : PersistentStateComponent<KotlinCompilerWorkspaceSettings> {
    /**
     * incrementalCompilationForJvmEnabled
     * (name `preciseIncrementalEnabled` is kept for workspace file compatibility)
     */
    var preciseIncrementalEnabled: Boolean = true
    var incrementalCompilationForJsEnabled: Boolean = true
    var enableDaemon: Boolean = true

    override fun getState(): KotlinCompilerWorkspaceSettings {
        return this
    }

    override fun loadState(state: KotlinCompilerWorkspaceSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
