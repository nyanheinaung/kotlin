/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.parsing

import org.jetbrains.kotlin.contracts.description.EffectDeclaration
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.BindingTrace

internal interface PsiEffectParser {
    fun tryParseEffect(expression: KtExpression): EffectDeclaration?
}

internal abstract class AbstractPsiEffectParser(
    val collector: ContractParsingDiagnosticsCollector,
    val callContext: ContractCallContext,
    val contractParserDispatcher: PsiContractParserDispatcher
) : PsiEffectParser
