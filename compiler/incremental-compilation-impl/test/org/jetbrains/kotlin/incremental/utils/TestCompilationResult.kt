/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental.utils

import org.jetbrains.kotlin.cli.common.ExitCode
import java.io.File

data class TestCompilationResult(
    val exitCode: ExitCode,
    val compiledSources: Iterable<File>,
    val compileErrors: Collection<String>
) {
    constructor(
        icReporter: TestICReporter,
        messageCollector: TestMessageCollector
    ) : this(icReporter.exitCode, icReporter.compiledSources, messageCollector.errors)
}