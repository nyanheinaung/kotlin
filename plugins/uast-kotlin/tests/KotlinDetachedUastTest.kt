/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.test.kotlin

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.testFramework.LightProjectDescriptor
import junit.framework.TestCase
import org.jetbrains.kotlin.asJava.elements.KtLightElement
import org.jetbrains.kotlin.idea.core.copied
import org.jetbrains.kotlin.idea.test.KotlinLightCodeInsightFixtureTestCase
import org.jetbrains.kotlin.idea.test.KotlinWithJdkAndRuntimeLightProjectDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.uast.*
import org.jetbrains.uast.test.env.kotlin.findUElementByTextFromPsi

class KotlinDetachedUastTest : KotlinLightCodeInsightFixtureTestCase() {

    override fun getProjectDescriptor(): LightProjectDescriptor = KotlinWithJdkAndRuntimeLightProjectDescriptor.INSTANCE

    fun testLiteralInAnnotation() {

        val psiFile = myFixture.configureByText("AnnotatedClass.kt", """
            class AnnotatedClass {
                    @JvmName(name = "")
                    fun bar(param: String) = null
            }
        """.trimIndent())

        fun psiElement(file: PsiFile): PsiElement = file.findElementAt(file.text.indexOf("\"\"")).orFail("literal")
                .getParentOfType<PsiLanguageInjectionHost>(false).orFail("host")
                .toUElement().orFail("uElement").getParentOfType<UClass>(false)
                .orFail("UClass").psi.orFail("psi")

        psiElement(psiFile).let {
            // Otherwise following asserts have no sense
            TestCase.assertTrue("psi element should be light ", it is KtLightElement<*, *>)
        }
        val copied = psiFile.copied()
        TestCase.assertNull("virtual file for copy should be null", copied.virtualFile)
        TestCase.assertNotNull("psi element in copy", psiElement(copied))
        TestCase.assertSame("copy.originalFile should be psiFile", copied.originalFile, psiFile)
        TestCase.assertSame("virtualFiles of element and file itself should be the same",
                            psiElement(copied).containingFile.originalFile.virtualFile,
                            copied.originalFile.virtualFile)
    }

    fun testDetachedResolve() {
        val psiFile = myFixture.configureByText(
            "AnnotatedClass.kt", """
            class AnnotatedClass {
                    @JvmName(name = "")
                    fun bar(param: String) { unknownFunc(param) }
            }
        """.trimIndent()
        ) as KtFile

        val detachedCall = psiFile.findDescendantOfType<KtCallExpression>()!!.copied()
        val uCallExpression = detachedCall.toUElementOfType<UCallExpression>()!!
        // at least it should not throw exceptions
        TestCase.assertNull(uCallExpression.methodName)
    }

    fun testCapturedTypeInExtensionReceiverOfCall() {
        val psiFile = myFixture.configureByText(
            "foo.kt", """
            class Foo<T>

            fun <K> K.extensionFunc() {}

            fun test(f: Foo<*>) {
                f.extensionFunc()
            }
        """.trimIndent()
        ) as KtFile

        val extensionFunctionCall = psiFile.findDescendantOfType<KtCallExpression>()!!
        val uCallExpression = extensionFunctionCall.toUElementOfType<UCallExpression>()!!

        TestCase.assertNotNull(uCallExpression.receiverType)
        TestCase.assertNotNull(uCallExpression.methodName)
    }

    fun testParameterInAnnotationClassFromFactory() {

        val detachedClass = KtPsiFactory(project).createClass("""
        annotation class MyAnnotation(val myParam: String = "default")
        """)

        detachedClass.findUElementByTextFromPsi<UElement>("default")
                .getParentOfType<UExpression>().let {
            TestCase.assertNotNull("it should return something at least", it)
        }

    }

    fun testLiteralInClassInitializerFromFactory() {

        val detachedClass = KtPsiFactory(project).createClass("""
        class MyAnnotation(){
            init {
                "default"
            }
        }
        """)

        val literalInside = detachedClass.findUElementByTextFromPsi<UElement>("default")
        generateSequence(literalInside, { it.uastParent }).count().let {
            TestCase.assertTrue("it should have some parents $it actually", it > 1)
        }

    }

}

fun <T> T?.orFail(msg: String): T = this ?: error(msg)