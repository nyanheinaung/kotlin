/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.refactoring.changeSignature.usages

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.usageView.UsageInfo
import org.jetbrains.kotlin.idea.refactoring.changeSignature.KotlinChangeInfo

abstract class KotlinUsageInfo<T : PsiElement> : UsageInfo {
    constructor(element: T) : super(element)
    constructor(reference: PsiReference) : super(reference)

    @Suppress("UNCHECKED_CAST")
    override fun getElement() = super.getElement() as T?

    open fun preprocessUsage() {}

    abstract fun processUsage(changeInfo: KotlinChangeInfo, element: T, allUsages: Array<out UsageInfo>): Boolean
}
