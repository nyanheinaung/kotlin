/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.noarg

import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.codegen.AbstractBytecodeListingTest
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor

abstract class AbstractBytecodeListingTestForNoArg : AbstractBytecodeListingTest() {
    internal companion object {
        val NOARG_ANNOTATIONS = listOf("NoArg", "NoArg2", "test.NoArg")
    }

    override fun setupEnvironment(environment: KotlinCoreEnvironment) {
        val project = environment.project
        StorageComponentContainerContributor.registerExtension(project, CliNoArgComponentContainerContributor(NOARG_ANNOTATIONS))
        ExpressionCodegenExtension.registerExtension(project, NoArgExpressionCodegenExtension(false))
    }
}