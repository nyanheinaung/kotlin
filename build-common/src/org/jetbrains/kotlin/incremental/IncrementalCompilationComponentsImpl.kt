/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental

import org.jetbrains.kotlin.load.kotlin.incremental.components.IncrementalCache
import org.jetbrains.kotlin.load.kotlin.incremental.components.IncrementalCompilationComponents
import org.jetbrains.kotlin.modules.TargetId

class IncrementalCompilationComponentsImpl(
        private val caches: Map<TargetId, IncrementalCache>
): IncrementalCompilationComponents {
    override fun getIncrementalCache(target: TargetId): IncrementalCache =
            caches[target] ?: throw Exception("Incremental cache for target ${target.name} not found")
}
