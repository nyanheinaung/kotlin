/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("unused")

package kotlin.script.experimental.dependencies

import kotlin.script.dependencies.Environment
import kotlin.script.dependencies.ScriptContents

interface AsyncDependenciesResolver : DependenciesResolver {
    suspend fun resolveAsync(
            scriptContents: ScriptContents, environment: Environment
    ): DependenciesResolver.ResolveResult

    /* 'resolve' implementation is supposed to invoke resolveAsync in a blocking manner
    *  To avoid dependency on kotlinx-coroutines-core we leave this method unimplemented
    *     and provide 'resolve' implementation when loading resolver.
    */
    override fun resolve(scriptContents: ScriptContents, environment: Environment): DependenciesResolver.ResolveResult
            = /*runBlocking { resolveAsync(scriptContents, environment) } */ throw NotImplementedError()
}