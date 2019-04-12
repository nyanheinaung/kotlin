/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android

import com.intellij.psi.xml.XmlFile
import org.jetbrains.kotlin.android.synthetic.AndroidConst
import org.jetbrains.kotlin.psi.KtFile

abstract class AbstractAndroidLayoutRenameTest : KotlinAndroidTestCase() {
    private val NEW_NAME = "new_name"
    private val NEW_NAME_XML = "$NEW_NAME.xml"

    fun doTest(path: String) {
        copyResourceDirectoryForTest(path)
        val virtualFile = myFixture.copyFileToProject(path + getTestName(true) + ".kt", "src/" + getTestName(true) + ".kt")
        myFixture.configureFromExistingVirtualFile(virtualFile)
        val xmlFile = myFixture.elementAtCaret.containingFile as XmlFile

        myFixture.renameElement(xmlFile, NEW_NAME_XML)

        val expectedImportName = AndroidConst.SYNTHETIC_PACKAGE + ".main." + NEW_NAME
        assertTrue((myFixture.file as KtFile).importDirectives.any { it.importedFqName?.asString() == expectedImportName })
    }
}
