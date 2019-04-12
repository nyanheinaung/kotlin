/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental

import org.jetbrains.kotlin.cli.common.arguments.K2JSCompilerArguments
import org.jetbrains.kotlin.incremental.testingUtils.BuildLogFinder
import org.jetbrains.kotlin.incremental.utils.TestCompilationResult
import org.jetbrains.kotlin.incremental.utils.TestICReporter
import org.jetbrains.kotlin.incremental.utils.TestMessageCollector
import java.io.File

abstract class AbstractIncrementalJsCompilerRunnerTest : AbstractIncrementalCompilerRunnerTestBase<K2JSCompilerArguments>() {
    override fun make(cacheDir: File, sourceRoots: Iterable<File>, args: K2JSCompilerArguments): TestCompilationResult {
        val reporter = TestICReporter()
        val messageCollector = TestMessageCollector()
        makeJsIncrementally(cacheDir, sourceRoots, args, reporter = reporter, messageCollector = messageCollector)
        return TestCompilationResult(reporter, messageCollector)
    }

    override val buildLogFinder: BuildLogFinder
        get() = super.buildLogFinder.copy(isJsEnabled = true)

    override fun createCompilerArguments(destinationDir: File, testDir: File): K2JSCompilerArguments =
        K2JSCompilerArguments().apply {
            outputFile = File(destinationDir, "${testDir.name}.js").path
            sourceMap = true
            metaInfo = true
        }
}