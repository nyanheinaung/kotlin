/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.codegen.state.GenerationState
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.resolve.jvm.jvmSignature.JvmMethodSignature

class ClosureGenerationStrategy(
        state: GenerationState,
        val declaration: KtDeclarationWithBody
) : FunctionGenerationStrategy.FunctionDefault(state, declaration) {

    override fun doGenerateBody(codegen: ExpressionCodegen, signature: JvmMethodSignature) {
        initializeVariablesForDestructuredLambdaParameters(codegen, codegen.context.functionDescriptor.valueParameters)
        if (declaration is KtFunctionLiteral) {
            recordCallLabelForLambdaArgument(declaration, state.bindingTrace)
        }
        super.doGenerateBody(codegen, signature)
    }
}
