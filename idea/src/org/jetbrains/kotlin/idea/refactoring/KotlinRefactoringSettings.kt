/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.refactoring

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "KotlinRefactoringSettings", storages = arrayOf(Storage("kotlinRefactoring.xml")))
class KotlinRefactoringSettings : PersistentStateComponent<KotlinRefactoringSettings> {
    @JvmField var MOVE_TO_UPPER_LEVEL_SEARCH_IN_COMMENTS = false
    @JvmField var MOVE_TO_UPPER_LEVEL_SEARCH_FOR_TEXT = false

    override fun getState() = this

    override fun loadState(state: KotlinRefactoringSettings) = XmlSerializerUtil.copyBean(state, this)

    companion object {
        @JvmStatic
        val instance: KotlinRefactoringSettings
            get() = ServiceManager.getService(KotlinRefactoringSettings::class.java)
    }
}