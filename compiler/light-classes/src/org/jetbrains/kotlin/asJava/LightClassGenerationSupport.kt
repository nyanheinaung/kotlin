/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.asJava

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.asJava.builder.LightClassBuilderResult
import org.jetbrains.kotlin.asJava.builder.LightClassConstructionContext
import org.jetbrains.kotlin.asJava.builder.LightClassDataHolder
import org.jetbrains.kotlin.asJava.classes.KtUltraLightClass
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

typealias LightClassBuilder = (LightClassConstructionContext) -> LightClassBuilderResult

abstract class LightClassGenerationSupport {
    abstract fun createDataHolderForClass(classOrObject: KtClassOrObject, builder: LightClassBuilder): LightClassDataHolder.ForClass

    abstract fun createDataHolderForFacade(files: Collection<KtFile>, builder: LightClassBuilder): LightClassDataHolder.ForFacade

    abstract fun createDataHolderForScript(script: KtScript, builder: LightClassBuilder): LightClassDataHolder.ForScript

    abstract fun resolveToDescriptor(declaration: KtDeclaration): DeclarationDescriptor?

    abstract fun analyze(element: KtElement): BindingContext

    abstract fun analyzeWithContent(element: KtClassOrObject): BindingContext

    abstract fun createUltraLightClass(element: KtClassOrObject): KtUltraLightClass?

    companion object {
        @JvmStatic
        fun getInstance(project: Project): LightClassGenerationSupport {
            return ServiceManager.getService(project, LightClassGenerationSupport::class.java)
        }
    }
}
