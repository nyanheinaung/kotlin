/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.generators.tests

import org.jetbrains.kotlin.checkers.AbstractForeignJava8AnnotationsNoAnnotationInClasspathTest
import org.jetbrains.kotlin.checkers.AbstractForeignJava8AnnotationsNoAnnotationInClasspathWithFastClassReadingTest
import org.jetbrains.kotlin.checkers.AbstractForeignJava8AnnotationsTest
import org.jetbrains.kotlin.checkers.javac.AbstractJavacForeignJava8AnnotationsTest
import org.jetbrains.kotlin.codegen.AbstractBytecodeTextTest
import org.jetbrains.kotlin.codegen.AbstractCompileKotlinAgainstKotlinTest
import org.jetbrains.kotlin.codegen.flags.AbstractWriteFlagsTest
import org.jetbrains.kotlin.generators.tests.generator.testGroup
import org.jetbrains.kotlin.jvm.compiler.AbstractLoadJava8Test
import org.jetbrains.kotlin.jvm.compiler.AbstractLoadJava8WithFastClassReadingTest
import org.jetbrains.kotlin.jvm.compiler.javac.AbstractLoadJava8UsingJavacTest
import org.jetbrains.kotlin.resolve.calls.AbstractEnhancedSignaturesResolvedCallsTest

fun main(args: Array<String>) {
    System.setProperty("java.awt.headless", "true")

    testGroup("compiler/tests-java8/tests", "compiler/testData") {
        testClass<AbstractForeignJava8AnnotationsTest> {
            model("foreignAnnotationsJava8/tests")
        }

        testClass<AbstractJavacForeignJava8AnnotationsTest> {
            model("foreignAnnotationsJava8/tests")
        }

        testClass<AbstractForeignJava8AnnotationsNoAnnotationInClasspathTest> {
            model("foreignAnnotationsJava8/tests")
        }

        testClass<AbstractForeignJava8AnnotationsNoAnnotationInClasspathWithFastClassReadingTest> {
            model("foreignAnnotationsJava8/tests")
        }

        testClass<AbstractLoadJava8Test> {
            model("loadJava8/compiledJava", extension = "java", testMethod = "doTestCompiledJava")
            model("loadJava8/sourceJava", extension = "java", testMethod = "doTestSourceJava")
        }

        testClass<AbstractLoadJava8UsingJavacTest> {
            model("loadJava8/compiledJava", extension = "java", testMethod = "doTestCompiledJava")
            model("loadJava8/sourceJava", extension = "java", testMethod = "doTestSourceJava")
        }

        testClass<AbstractLoadJava8WithFastClassReadingTest> {
            model("loadJava8/compiledJava", extension = "java", testMethod = "doTestCompiledJava")
        }

        testClass<AbstractEnhancedSignaturesResolvedCallsTest> {
            model("resolvedCalls/enhancedSignatures")
        }
    }
}
