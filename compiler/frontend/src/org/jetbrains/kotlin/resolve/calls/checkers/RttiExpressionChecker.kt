/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.types.KotlinType

interface RttiExpressionChecker {
    fun check(rttiInformation: RttiExpressionInformation, reportOn: PsiElement, trace: BindingTrace)
}

enum class RttiOperation {
    IS,
    NOT_IS,
    AS,
    SAFE_AS
}

class RttiExpressionInformation(
    val subject: KtElement,
    val sourceType: KotlinType?,
    val targetType: KotlinType?,
    val operation: RttiOperation
)