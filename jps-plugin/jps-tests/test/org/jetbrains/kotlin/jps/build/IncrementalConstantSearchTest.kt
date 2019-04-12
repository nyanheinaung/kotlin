/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jps.build

class IncrementalConstantSearchTest : AbstractIncrementalJpsTest() {
    fun testJavaConstantChangedUsedInKotlin() {
        doTest("jps-plugin/testData/incremental/custom/javaConstantChangedUsedInKotlin/")
    }

    fun testJavaConstantUnchangedUsedInKotlin() {
        doTest("jps-plugin/testData/incremental/custom/javaConstantUnchangedUsedInKotlin/")
    }

    fun testKotlinConstantChangedUsedInJava() {
        doTest("jps-plugin/testData/incremental/custom/kotlinConstantChangedUsedInJava/")
    }

    fun testKotlinJvmFieldChangedUsedInJava() {
        doTest("jps-plugin/testData/incremental/custom/kotlinJvmFieldChangedUsedInJava/")
    }

    fun testKotlinConstantUnchangedUsedInJava() {
        doTest("jps-plugin/testData/incremental/custom/kotlinConstantUnchangedUsedInJava/")
    }

    fun testKotlinJvmFieldUnchangedUsedInJava() {
        doTest("jps-plugin/testData/incremental/custom/kotlinJvmFieldUnchangedUsedInJava/")
    }
}