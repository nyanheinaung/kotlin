/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.synthetic.idea

import com.intellij.diagnostic.ITNReporter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent

class AndroidExtensionsReportSubmitter : ITNReporter() {
    override fun showErrorInRelease(event: IdeaLoggingEvent) = true
}