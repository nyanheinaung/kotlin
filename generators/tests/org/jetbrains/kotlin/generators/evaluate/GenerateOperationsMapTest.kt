/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.generators.test.evaluate

import com.intellij.testFramework.UsefulTestCase
import org.jetbrains.kotlin.generators.evaluate.DEST_FILE
import org.jetbrains.kotlin.generators.evaluate.generate
import org.jetbrains.kotlin.test.KotlinTestUtils

class GenerateOperationsMapTest : UsefulTestCase() {
    fun testGeneratedDataIsUpToDate(): Unit {
        val text = generate()
        KotlinTestUtils.assertEqualsToFile(DEST_FILE, text)
    }
}
