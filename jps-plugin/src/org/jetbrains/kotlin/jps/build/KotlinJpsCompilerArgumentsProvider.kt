/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jps.build

import org.jetbrains.jps.incremental.CompileContext
import org.jetbrains.jps.incremental.ModuleBuildTarget

interface KotlinJpsCompilerArgumentsProvider {
    fun getExtraArguments(moduleBuildTarget: ModuleBuildTarget, context: CompileContext): List<String>
    fun getClasspath(moduleBuildTarget: ModuleBuildTarget, context: CompileContext): List<String>
}