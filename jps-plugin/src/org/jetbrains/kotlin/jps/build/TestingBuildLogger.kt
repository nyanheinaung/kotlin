/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jps.build

import org.jetbrains.jps.incremental.CompileContext
import org.jetbrains.jps.incremental.ModuleLevelBuilder
import org.jetbrains.kotlin.jps.incremental.CacheAttributesDiff
import org.jetbrains.kotlin.jps.targets.KotlinModuleBuildTarget
import java.io.File

/**
 * Used for assertions in tests.
 */
interface TestingBuildLogger {
    fun invalidOrUnusedCache(chunk: KotlinChunk?, target: KotlinModuleBuildTarget<*>?, attributesDiff: CacheAttributesDiff<*>)
    fun chunkBuildStarted(context: CompileContext, chunk: org.jetbrains.jps.ModuleChunk)
    fun afterChunkBuildStarted(context: CompileContext, chunk: org.jetbrains.jps.ModuleChunk)
    fun addCustomMessage(message: String)
    fun buildFinished(exitCode: ModuleLevelBuilder.ExitCode)
    fun markedAsDirtyBeforeRound(files: Iterable<File>)
    fun markedAsDirtyAfterRound(files: Iterable<File>)
}
