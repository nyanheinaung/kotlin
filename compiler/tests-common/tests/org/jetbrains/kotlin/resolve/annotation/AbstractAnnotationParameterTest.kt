/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.annotation

import com.intellij.openapi.util.io.FileUtil
import org.jetbrains.kotlin.test.InTextDirectivesUtils
import org.jetbrains.kotlin.test.KotlinTestUtils
import java.io.File

abstract class AbstractAnnotationParameterTest : AbstractAnnotationDescriptorResolveTest() {
    fun doTest(path: String) {
        val fileText = FileUtil.loadFile(File(path), true)
        val packageView = getPackage(fileText)
        val classDescriptor = AbstractAnnotationDescriptorResolveTest.getClassDescriptor(packageView, "MyClass")

        val expected = InTextDirectivesUtils.findListWithPrefixes(fileText, "// EXPECTED: ").joinToString(", ")
        val actual = AbstractAnnotationDescriptorResolveTest.renderAnnotations(classDescriptor.annotations)

        KotlinTestUtils.assertEqualsToFile(File(path), fileText.replace(expected, actual))
    }
}
