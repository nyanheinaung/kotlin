/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.allopen

import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.codegen.AbstractBytecodeListingTest
import org.jetbrains.kotlin.extensions.DeclarationAttributeAltererExtension

abstract class AbstractBytecodeListingTestForAllOpen : AbstractBytecodeListingTest() {
    override fun setupEnvironment(environment: KotlinCoreEnvironment) {
        val annotations = AbstractAllOpenDeclarationAttributeAltererExtension.ANNOTATIONS_FOR_TESTS +
                          AllOpenCommandLineProcessor.SUPPORTED_PRESETS.flatMap { it.value }

        DeclarationAttributeAltererExtension.registerExtension(
                environment.project,
                CliAllOpenDeclarationAttributeAltererExtension(annotations))
    }
}