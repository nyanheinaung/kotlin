/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.progress

import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.progress.ProgressIndicatorProvider

class CompilationCanceledException : ProcessCanceledException()

interface CompilationCanceledStatus {
    fun checkCanceled(): Unit
}

object ProgressIndicatorAndCompilationCanceledStatus {
    private var canceledStatus: CompilationCanceledStatus? = null

    @JvmStatic
    @Synchronized fun setCompilationCanceledStatus(newCanceledStatus: CompilationCanceledStatus?): Unit {
        canceledStatus = newCanceledStatus
    }

    @JvmStatic fun checkCanceled(): Unit {
        ProgressIndicatorProvider.checkCanceled()
        canceledStatus?.checkCanceled()
    }
}