/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.compilerRunner

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.Services
import org.jetbrains.kotlin.preloading.ClassCondition
import org.jetbrains.kotlin.utils.KotlinPaths

class JpsCompilerEnvironment(
    val kotlinPaths: KotlinPaths,
    services: Services,
    val classesToLoadByParent: ClassCondition,
    messageCollector: MessageCollector,
    outputItemsCollector: OutputItemsCollectorImpl,
    val progressReporter: ProgressReporter
) : CompilerEnvironment(services, messageCollector, outputItemsCollector) {
    override val outputItemsCollector: OutputItemsCollectorImpl
        get() = super.outputItemsCollector as OutputItemsCollectorImpl
}
