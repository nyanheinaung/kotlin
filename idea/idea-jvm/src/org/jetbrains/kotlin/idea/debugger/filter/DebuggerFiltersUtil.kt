/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.debugger.filter

import com.intellij.debugger.settings.DebuggerSettings
import com.intellij.ui.classFilter.ClassFilter
import org.jetbrains.kotlin.idea.debugger.KotlinDebuggerSettings

private val KOTLIN_STDLIB_FILTER = "kotlin.*"

fun addKotlinStdlibDebugFilterIfNeeded() {
    if (!KotlinDebuggerSettings.getInstance().DEBUG_IS_FILTER_FOR_STDLIB_ALREADY_ADDED) {
        val settings = DebuggerSettings.getInstance()!!
        val newFilters = (settings.steppingFilters + ClassFilter(KOTLIN_STDLIB_FILTER))

        settings.steppingFilters = newFilters

        KotlinDebuggerSettings.getInstance().DEBUG_IS_FILTER_FOR_STDLIB_ALREADY_ADDED = true
    }
}

