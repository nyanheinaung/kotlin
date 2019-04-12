/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.tests.di

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.getValue
import org.jetbrains.kotlin.container.useImpl
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.context.ModuleContext
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.frontend.di.configureModule
import org.jetbrains.kotlin.resolve.*
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactory
import org.jetbrains.kotlin.resolve.jvm.platform.JvmPlatform
import org.jetbrains.kotlin.types.expressions.ExpressionTypingServices
import org.jetbrains.kotlin.types.expressions.FakeCallResolver

fun createContainerForTests(project: Project, module: ModuleDescriptor): ContainerForTests {
    return ContainerForTests(createContainer("Tests", JvmPlatform) {
        configureModule(ModuleContext(module, project), JvmPlatform, JvmTarget.DEFAULT)
        useInstance(LanguageVersionSettingsImpl.DEFAULT)
        useImpl<AnnotationResolverImpl>()
        useImpl<ExpressionTypingServices>()
    })
}

class ContainerForTests(container: StorageComponentContainer) {
    val descriptorResolver: DescriptorResolver by container
    val functionDescriptorResolver: FunctionDescriptorResolver by container
    val typeResolver: TypeResolver by container
    val fakeCallResolver: FakeCallResolver by container
    val expressionTypingServices: ExpressionTypingServices by container
    val dataFlowValueFactory: DataFlowValueFactory by container
}
