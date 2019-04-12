/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.compilerRunner

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.Services
import org.jetbrains.kotlin.utils.KotlinPaths

open class CompilerEnvironment(
        val services: Services,
        val messageCollector: MessageCollector,
        open val outputItemsCollector: OutputItemsCollector
)

