/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.console.gutter

import com.intellij.openapi.editor.markup.GutterIconRenderer

class ConsoleIndicatorRenderer(iconWithTooltip: IconWithTooltip) : GutterIconRenderer() {
    private val icon = iconWithTooltip.icon
    private val tooltip = iconWithTooltip.tooltip

    override fun getIcon() = icon
    override fun getTooltipText() = tooltip

    override fun hashCode() = icon.hashCode()
    override fun equals(other: Any?) = icon == (other as? ConsoleIndicatorRenderer)?.icon
}