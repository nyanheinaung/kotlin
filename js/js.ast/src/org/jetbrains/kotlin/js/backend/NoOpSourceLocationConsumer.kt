/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.backend

object NoOpSourceLocationConsumer : SourceLocationConsumer {
    override fun newLine() {}

    override fun pushSourceInfo(info: Any?) {}

    override fun popSourceInfo() {}
}