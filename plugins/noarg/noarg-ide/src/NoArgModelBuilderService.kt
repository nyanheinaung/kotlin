/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.noarg.ide

import com.intellij.openapi.util.Key
import org.jetbrains.kotlin.annotation.plugin.ide.*

interface NoArgModel : AnnotationBasedPluginModel {
    val invokeInitializers: Boolean

    override fun dump(): DumpedPluginModel {
        return DumpedPluginModelImpl(NoArgModelImpl::class.java, annotations.toList(), presets.toList(), invokeInitializers)
    }
}

class NoArgModelImpl(
        override val annotations: List<String>,
        override val presets: List<String>,
        override val invokeInitializers: Boolean
) : NoArgModel

class NoArgModelBuilderService : AnnotationBasedPluginModelBuilderService<NoArgModel>() {
    override val gradlePluginNames get() = listOf("org.jetbrains.kotlin.plugin.noarg", "kotlin-noarg")
    override val extensionName get() = "noArg"
    override val modelClass get() = NoArgModel::class.java

    override fun createModel(annotations: List<String>, presets: List<String>, extension: Any?): NoArgModel {
        val invokeInitializers = extension?.getFieldValue("invokeInitializers") as? Boolean ?: false
        return NoArgModelImpl(annotations, presets, invokeInitializers)
    }
}

class NoArgProjectResolverExtension : AnnotationBasedPluginProjectResolverExtension<NoArgModel>() {
    companion object {
        val KEY = Key<NoArgModel>("NoArgModel")
    }

    override val modelClass get() = NoArgModel::class.java
    override val userDataKey get() = KEY
}