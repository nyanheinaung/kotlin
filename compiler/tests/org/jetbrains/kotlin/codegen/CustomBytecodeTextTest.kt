/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.test.ConfigurationKind
import org.jetbrains.kotlin.test.testFramework.KtUsefulTestCase

class CustomBytecodeTextTest : AbstractBytecodeTextTest() {
    fun testEnumMapping() {
        createEnvironmentWithMockJdkAndIdeaAnnotations(ConfigurationKind.ALL)
        myFiles = CodegenTestFiles.create("whenMappingOrder.kt", """
        enum class MyEnum {
            ENTRY1, ENTRY2, ENTRY3, ENTRY4
        }

        fun f(e: MyEnum) {
            when (e) {
                MyEnum.ENTRY4 -> {}
                MyEnum.ENTRY3 -> {}
                MyEnum.ENTRY2 -> {}
                MyEnum.ENTRY1 -> {}
            }
        }
        """, myEnvironment.project)

        val text = generateToText()
        val getstatics = text.lines().filter { it.contains("GETSTATIC MyEnum.") }.map { it.trim() }
        KtUsefulTestCase.assertOrderedEquals("actual bytecode:\n" + text, listOf(
                "GETSTATIC MyEnum.${'$'}VALUES : [LMyEnum;",
                "GETSTATIC MyEnum.ENTRY4 : LMyEnum;",
                "GETSTATIC MyEnum.ENTRY3 : LMyEnum;",
                "GETSTATIC MyEnum.ENTRY2 : LMyEnum;",
                "GETSTATIC MyEnum.ENTRY1 : LMyEnum;"
        ), getstatics)
    }
}