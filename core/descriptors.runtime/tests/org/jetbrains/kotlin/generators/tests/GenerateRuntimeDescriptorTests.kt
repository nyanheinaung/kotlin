/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.generators.tests

import org.jetbrains.kotlin.generators.tests.generator.testGroup
import org.jetbrains.kotlin.jvm.runtime.AbstractJvm8RuntimeDescriptorLoaderTest
import org.jetbrains.kotlin.jvm.runtime.AbstractJvmRuntimeDescriptorLoaderTest

fun main() {
    System.setProperty("java.awt.headless", "true")

    testGroup("core/descriptors.runtime/tests", "compiler/testData") {
        testClass<AbstractJvmRuntimeDescriptorLoaderTest> {
            model("loadJava/compiledKotlin")
            model("loadJava/compiledJava", extension = "java", excludeDirs = listOf("sam", "kotlinSignature/propagation"))
        }

        testClass<AbstractJvm8RuntimeDescriptorLoaderTest> {
            model("loadJava8/compiledJava", extension = "java")
        }
    }
}
