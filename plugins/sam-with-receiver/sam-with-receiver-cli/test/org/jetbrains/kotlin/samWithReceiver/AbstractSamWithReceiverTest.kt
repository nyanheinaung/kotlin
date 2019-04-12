/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.samWithReceiver

import org.jetbrains.kotlin.checkers.AbstractDiagnosticsTest
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import java.io.File

abstract class AbstractSamWithReceiverTest : AbstractDiagnosticsTest() {
    private companion object {
        private val TEST_ANNOTATIONS = listOf("SamWithReceiver")
    }

    override fun createEnvironment(file: File) = super.createEnvironment(file).apply {
        StorageComponentContainerContributor.registerExtension(project, CliSamWithReceiverComponentContributor(TEST_ANNOTATIONS))
    }
}
