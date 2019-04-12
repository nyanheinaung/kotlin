/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.generators.protobuf

import com.intellij.openapi.util.io.FileUtil
import com.intellij.testFramework.UsefulTestCase
import org.jetbrains.kotlin.test.KotlinTestUtils
import java.io.File

class ProtoBufCompareConsistencyTest : UsefulTestCase() {
    fun testAlreadyGenerated() {
        val testDir = KotlinTestUtils.tmpDir("testDirectory")
        val newFile = File(testDir, "ProtoCompareGenerated.kt")
        GenerateProtoBufCompare.generate(newFile)

        KotlinTestUtils.assertEqualsToFile(newFile, FileUtil.loadFile(GenerateProtoBufCompare.DEST_FILE))
    }
}
