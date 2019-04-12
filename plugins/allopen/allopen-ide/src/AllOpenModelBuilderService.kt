/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.allopen.ide

import com.intellij.openapi.util.Key
import org.jetbrains.kotlin.annotation.plugin.ide.*

interface AllOpenModel : AnnotationBasedPluginModel {
    override fun dump(): DumpedPluginModel {
        return DumpedPluginModelImpl(AllOpenModelImpl::class.java, annotations.toList(), presets.toList())
    }
}

class AllOpenModelImpl(
        override val annotations: List<String>,
        override val presets: List<String>
) : AllOpenModel

class AllOpenModelBuilderService : AnnotationBasedPluginModelBuilderService<AllOpenModel>() {
    override val gradlePluginNames get() = listOf("org.jetbrains.kotlin.plugin.allopen", "kotlin-allopen")
    override val extensionName get() = "allOpen"
    override val modelClass get() = AllOpenModel::class.java

    override fun createModel(annotations: List<String>, presets: List<String>, extension: Any?): AllOpenModelImpl {
        return AllOpenModelImpl(annotations, presets)
    }
}

class AllOpenProjectResolverExtension : AnnotationBasedPluginProjectResolverExtension<AllOpenModel>() {
    companion object {
        val KEY = Key<AllOpenModel>("AllOpenModel")
    }

    override val modelClass get() = AllOpenModel::class.java
    override val userDataKey get() = KEY
}