/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.script

import kotlinx.coroutines.runBlocking
import kotlin.script.experimental.dependencies.DependenciesResolver
import kotlin.script.dependencies.Environment
import kotlin.script.dependencies.ScriptContents
import kotlin.script.experimental.dependencies.AsyncDependenciesResolver

// wraps AsyncDependenciesResolver to provide implementation for synchronous DependenciesResolver::resolve
class AsyncDependencyResolverWrapper(
        override val delegate: AsyncDependenciesResolver
): AsyncDependenciesResolver, DependencyResolverWrapper<AsyncDependenciesResolver> {

    override fun resolve(
            scriptContents: ScriptContents, environment: Environment
    ): DependenciesResolver.ResolveResult
            = runBlocking { delegate.resolveAsync(scriptContents, environment) }


    suspend override fun resolveAsync(
            scriptContents: ScriptContents, environment: Environment
    ): DependenciesResolver.ResolveResult
            = delegate.resolveAsync(scriptContents, environment)
}