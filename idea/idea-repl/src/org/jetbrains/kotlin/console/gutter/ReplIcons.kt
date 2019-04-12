/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.console.gutter

import com.intellij.icons.AllIcons
import org.jetbrains.kotlin.idea.KotlinIcons
import javax.swing.Icon

data class IconWithTooltip(val icon: Icon, val tooltip: String?)

object ReplIcons {
    val BUILD_WARNING_INDICATOR: IconWithTooltip = IconWithTooltip(AllIcons.Ide.Warning_notifications, null)
    val HISTORY_INDICATOR: IconWithTooltip = IconWithTooltip(AllIcons.General.MessageHistory, "History of executed commands")
    val EDITOR_INDICATOR: IconWithTooltip = IconWithTooltip(KotlinIcons.LAUNCH, "Write your commands here")
    val EDITOR_READLINE_INDICATOR: IconWithTooltip = IconWithTooltip(AllIcons.General.Balloon, "Waiting for input...")
    val COMMAND_MARKER: IconWithTooltip = IconWithTooltip(AllIcons.General.Run, "Executed command")
    val READLINE_MARKER: IconWithTooltip = IconWithTooltip(AllIcons.Icons.Ide.SpeedSearchPrompt, "Input line")

    // command result icons
    val SYSTEM_HELP: IconWithTooltip = IconWithTooltip(AllIcons.Actions.Menu_help, "System help")
    val RESULT: IconWithTooltip = IconWithTooltip(AllIcons.Vcs.Equal, "Result")
    val COMPILER_ERROR: Icon = AllIcons.General.Error
    val RUNTIME_EXCEPTION: IconWithTooltip = IconWithTooltip(AllIcons.General.BalloonWarning, "Runtime exception")
}