/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.project

import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useImpl
import org.jetbrains.kotlin.idea.caches.lightClasses.LazyLightClassDataHolder
import org.jetbrains.kotlin.resolve.TargetEnvironment

object IdeaEnvironment : TargetEnvironment("Idea") {
    override fun configure(container: StorageComponentContainer) {
        container.useImpl<ResolveElementCache>()
        container.useImpl<IdeaLocalDescriptorResolver>()
        container.useImpl<IdeaAbsentDescriptorHandler>()
        container.useImpl<LazyLightClassDataHolder.DiagnosticsHolder>()
    }
}
