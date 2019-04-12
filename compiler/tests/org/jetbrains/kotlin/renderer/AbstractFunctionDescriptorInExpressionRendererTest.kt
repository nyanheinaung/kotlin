/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.renderer

import com.intellij.openapi.editor.impl.DocumentImpl
import com.intellij.openapi.util.io.FileUtil
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.impl.FunctionExpressionDescriptor
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.JvmResolveUtil
import org.jetbrains.kotlin.test.ConfigurationKind
import org.jetbrains.kotlin.test.KotlinTestUtils
import org.jetbrains.kotlin.test.KotlinTestWithEnvironment
import org.jetbrains.kotlin.test.testFramework.KtUsefulTestCase
import org.jetbrains.kotlin.utils.addIfNotNull
import java.io.File

abstract class AbstractFunctionDescriptorInExpressionRendererTest : KotlinTestWithEnvironment() {
    fun doTest(path: String) {
        val fileText = FileUtil.loadFile(File(path), true)
        val file = KtPsiFactory(project).createFile(fileText)
        val bindingContext = JvmResolveUtil.analyze(file, environment).bindingContext

        val descriptors = arrayListOf<DeclarationDescriptor>()

        file.accept(object : KtTreeVisitorVoid() {
            override fun visitNamedFunction(function: KtNamedFunction) {
                function.acceptChildren(this)
                descriptors.addIfNotNull(bindingContext.get(BindingContext.FUNCTION, function) as? FunctionExpressionDescriptor)
            }
            override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
                lambdaExpression.acceptChildren(this)
                descriptors.add(bindingContext.get(BindingContext.FUNCTION, lambdaExpression.functionLiteral)!!)
            }
        })

        val renderer = DescriptorRenderer.withOptions {
            classifierNamePolicy = ClassifierNamePolicy.FULLY_QUALIFIED
            modifiers = DescriptorRendererModifier.ALL
            verbose = true
            annotationArgumentsRenderingPolicy = AnnotationArgumentsRenderingPolicy.UNLESS_EMPTY
        }
        val renderedDescriptors = descriptors.map { renderer.render(it) }.joinToString(separator = "\n")

        val document = DocumentImpl(file.text)
        KtUsefulTestCase.assertSameLines(KotlinTestUtils.getLastCommentedLines(document), renderedDescriptors.toString())
    }

    override fun createEnvironment(): KotlinCoreEnvironment {
        return createEnvironmentWithMockJdk(ConfigurationKind.JDK_ONLY)
    }
}