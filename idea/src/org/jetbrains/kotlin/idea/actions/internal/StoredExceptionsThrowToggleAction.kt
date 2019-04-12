/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.actions.internal

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.application.ApplicationManager
import org.jetbrains.kotlin.utils.WrappedValues


class StoredExceptionsThrowToggleAction : ToggleAction("Internal: toggle throwing cached PCE", "Rethrow stored PCE as a new runtime exception", null) {
    override fun isSelected(e: AnActionEvent): Boolean {
        return WrappedValues.throwWrappedProcessCanceledException
    }

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        WrappedValues.throwWrappedProcessCanceledException = state
    }

    override fun update(e: AnActionEvent) {
        super.update(e)

        e.presentation.isEnabled = ApplicationManager.getApplication().isInternal
    }
}
