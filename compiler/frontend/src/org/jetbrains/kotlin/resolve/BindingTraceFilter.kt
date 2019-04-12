/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve

class BindingTraceFilter(val ignoreDiagnostics: Boolean) {
    companion object {
        val ACCEPT_ALL = BindingTraceFilter(false)
        val NO_DIAGNOSTICS = BindingTraceFilter(true)
    }

    fun includesEverythingIn(otherFilter: BindingTraceFilter): Boolean {
        if (ignoreDiagnostics && !otherFilter.ignoreDiagnostics) {
            return false
        }
        return true
    }
}
