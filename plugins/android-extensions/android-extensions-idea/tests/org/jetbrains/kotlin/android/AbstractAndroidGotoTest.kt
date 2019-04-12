/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android

import com.intellij.codeInsight.TargetElementUtil
import com.intellij.codeInsight.navigation.actions.GotoDeclarationAction
import com.intellij.psi.xml.XmlAttributeValue
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.idea.caches.resolve.resolveToCall
import org.jetbrains.kotlin.psi.KtElement

abstract class AbstractAndroidGotoTest : KotlinAndroidTestCase() {

    fun doTest(path: String) {
        copyResourceDirectoryForTest(path)
        val virtualFile = myFixture.copyFileToProject(path + getTestName(true) + ".kt", "src/" + getTestName(true) + ".kt")
        myFixture.configureFromExistingVirtualFile(virtualFile)

        val expression = TargetElementUtil.findReference(myFixture.editor, myFixture.caretOffset)!!.element as KtElement
        val resolvedCall = expression.resolveToCall()!!
        val property = resolvedCall.resultingDescriptor as? PropertyDescriptor ?: throw AssertionError("PropertyDescriptor expected")

        val targetElement = GotoDeclarationAction.findTargetElement(myFixture.project, myFixture.editor, myFixture.caretOffset)!!

        assert(targetElement is XmlAttributeValue) { "XmlAttributeValue expected, got ${targetElement::class.java}" }
        assertEquals("@+id/${property.name}", (targetElement as XmlAttributeValue).value)
    }
}