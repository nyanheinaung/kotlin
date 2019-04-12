/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.samWithReceiver

import org.jetbrains.kotlin.checkers.AbstractDiagnosticsTest
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.script.KotlinScriptDefinitionFromAnnotatedTemplate
import java.io.File
import kotlin.script.extensions.SamWithReceiverAnnotations
import kotlin.script.templates.ScriptTemplateDefinition

abstract class AbstractSamWithReceiverScriptTest : AbstractDiagnosticsTest() {
    private companion object {
        private val TEST_ANNOTATIONS = emptyList<String>()
    }

    override fun createEnvironment(file: File) = super.createEnvironment(file).apply {
        StorageComponentContainerContributor.registerExtension(project, CliSamWithReceiverComponentContributor(TEST_ANNOTATIONS))
        val def = KotlinScriptDefinitionFromAnnotatedTemplate(ScriptForSamWithReceivers::class, emptyMap())
        configuration.add(JVMConfigurationKeys.SCRIPT_DEFINITIONS, def)
    }
}

@ScriptTemplateDefinition
@SamWithReceiverAnnotations("SamWithReceiver1", "SamWithReceiver2")
abstract class ScriptForSamWithReceivers
