/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.checkers

import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys

abstract class AbstractForeignAnnotationsNoAnnotationInClasspathWithFastClassReadingTest : AbstractForeignAnnotationsNoAnnotationInClasspathTest() {
    override fun performCustomConfiguration(configuration: CompilerConfiguration) {
        super.performCustomConfiguration(configuration)
        configuration.put(JVMConfigurationKeys.USE_FAST_CLASS_FILES_READING, true)
    }
}
