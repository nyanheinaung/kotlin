/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types.expressions

import com.google.common.collect.LinkedHashMultimap
import com.google.common.collect.SetMultimap
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.*

abstract class AssignedVariablesSearcher : KtTreeVisitorVoid() {

    data class Writer(val assignment: KtBinaryExpression, val declaration: KtDeclaration?)

    private val assignedNames: SetMultimap<Name, Writer> = LinkedHashMultimap.create()

    open fun writers(variableDescriptor: VariableDescriptor): MutableSet<Writer> = assignedNames[variableDescriptor.name]

    fun hasWriters(variableDescriptor: VariableDescriptor) = writers(variableDescriptor).isNotEmpty()

    private var currentDeclaration: KtDeclaration? = null

    override fun visitDeclaration(declaration: KtDeclaration) {
        val previous = currentDeclaration
        if (declaration is KtDeclarationWithBody || declaration is KtClassOrObject || declaration is KtAnonymousInitializer) {
            currentDeclaration = declaration
        }
        super.visitDeclaration(declaration)
        currentDeclaration = previous
    }

    override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
        val previous = currentDeclaration
        currentDeclaration = lambdaExpression.functionLiteral
        super.visitLambdaExpression(lambdaExpression)
        currentDeclaration = previous
    }

    override fun visitBinaryExpression(binaryExpression: KtBinaryExpression) {
        if (binaryExpression.operationToken === KtTokens.EQ) {
            val left = KtPsiUtil.deparenthesize(binaryExpression.left)
            if (left is KtNameReferenceExpression) {
                assignedNames.put(left.getReferencedNameAsName(), Writer(binaryExpression, currentDeclaration))
            }
        }
        super.visitBinaryExpression(binaryExpression)
    }
}