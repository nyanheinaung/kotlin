/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.test

import com.intellij.facet.FacetManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.roots.ModifiableRootModel
import org.jetbrains.kotlin.config.CompilerSettings
import org.jetbrains.kotlin.config.LanguageVersion
import org.jetbrains.kotlin.idea.facet.*
import org.jetbrains.kotlin.test.testFramework.runInEdtAndWait
import org.jetbrains.kotlin.test.testFramework.runWriteAction

class KotlinProjectDescriptorWithFacet(
    private val languageVersion: LanguageVersion,
    private val multiPlatform: Boolean = false
) : KotlinLightProjectDescriptor() {
    override fun configureModule(module: Module, model: ModifiableRootModel, contentEntry: ContentEntry) {
        configureKotlinFacet(module) {
            settings.languageLevel = languageVersion
            if (multiPlatform) {
                settings.compilerSettings = CompilerSettings().apply {
                    additionalArguments += " -Xmulti-platform"
                }
            }
        }
    }

    companion object {
        val KOTLIN_10 = KotlinProjectDescriptorWithFacet(LanguageVersion.KOTLIN_1_0)
        val KOTLIN_11 = KotlinProjectDescriptorWithFacet(LanguageVersion.KOTLIN_1_1)
        val KOTLIN_STABLE_WITH_MULTIPLATFORM = KotlinProjectDescriptorWithFacet(LanguageVersion.LATEST_STABLE, multiPlatform = true)
    }
}

fun configureKotlinFacet(module: Module, configureCallback: KotlinFacetConfiguration.() -> Unit = {}): KotlinFacet {
    val facetManager = FacetManager.getInstance(module)
    val facetModel = facetManager.createModifiableModel()
    val configuration = KotlinFacetConfigurationImpl()
    configuration.settings.initializeIfNeeded(module, null)
    configuration.settings.useProjectSettings = false
    configuration.configureCallback()
    val facet = facetManager.createFacet(KotlinFacetType.INSTANCE, "Kotlin", configuration, null)
    facetModel.addFacet(facet)
    runInEdtAndWait {
        runWriteAction { facetModel.commit() }
    }
    return facet
}

