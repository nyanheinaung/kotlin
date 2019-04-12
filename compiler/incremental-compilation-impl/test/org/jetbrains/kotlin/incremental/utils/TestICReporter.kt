/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental.utils

import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.incremental.ICReporterBase
import java.io.File

class TestICReporter : ICReporterBase() {
    private val compiledSourcesMutable = arrayListOf<File>()

    val compiledSources: List<File>
        get() = compiledSourcesMutable

    var exitCode: ExitCode = ExitCode.OK
        private set

    override fun report(message: () -> String) {
    }

    override fun reportVerbose(message: () -> String) {
    }

    override fun reportCompileIteration(incremental: Boolean, sourceFiles: Collection<File>, exitCode: ExitCode) {
        compiledSourcesMutable.addAll(sourceFiles)
        this.exitCode = exitCode
    }
}