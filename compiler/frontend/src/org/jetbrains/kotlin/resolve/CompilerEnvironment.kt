/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve

import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useImpl
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.resolve.lazy.CompilerLocalDescriptorResolver
import org.jetbrains.kotlin.resolve.lazy.BasicAbsentDescriptorHandler

object CompilerEnvironment : TargetEnvironment("Compiler") {
    override fun configure(container: StorageComponentContainer) {
        container.useInstance(BodyResolveCache.ThrowException)
        container.useImpl<CompilerLocalDescriptorResolver>()
        container.useImpl<BasicAbsentDescriptorHandler>()
    }
}