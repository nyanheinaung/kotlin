/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.console.gutter

import com.intellij.openapi.editor.markup.GutterIconRenderer
import org.jetbrains.kotlin.console.SeverityDetails
import org.jetbrains.kotlin.diagnostics.Severity

class ConsoleErrorRenderer(private val messages: List<SeverityDetails>) : GutterIconRenderer() {
    private fun msgType(severity: Severity) = when (severity) {
        Severity.ERROR -> "Error:"
        Severity.WARNING -> "Warning:"
        Severity.INFO -> "Info:"
    }

    override fun getTooltipText(): String {
        val htmlTooltips = messages.map { "<b>${msgType(it.severity)}</b> ${it.description}" }
        return "<html>${htmlTooltips.joinToString("<hr size=1 noshade>")}</html>"
    }

    override fun getIcon() = ReplIcons.COMPILER_ERROR
    override fun hashCode() = System.identityHashCode(this)
    override fun equals(other: Any?) = this === other
}