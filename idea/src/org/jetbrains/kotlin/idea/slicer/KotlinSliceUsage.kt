/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.slicer

import com.intellij.psi.PsiElement
import com.intellij.slicer.SliceAnalysisParams
import com.intellij.slicer.SliceUsage
import com.intellij.usageView.UsageInfo
import com.intellij.util.Processor
import org.jetbrains.kotlin.psi.KtExpression

open class KotlinSliceUsage : SliceUsage {
    class UsageInfoLambdaWrapper(element: PsiElement) : UsageInfo(element)

    val lambdaLevel: Int
    val forcedExpressionMode: Boolean

    constructor(element: PsiElement, parent: SliceUsage, lambdaLevel: Int, forcedExpressionMode: Boolean) : super(element, parent) {
        this.lambdaLevel = lambdaLevel
        this.forcedExpressionMode = forcedExpressionMode
    }

    constructor(element: PsiElement, params: SliceAnalysisParams) : super(element, params) {
        this.lambdaLevel = 0
        this.forcedExpressionMode = false
    }

    override fun copy(): KotlinSliceUsage {
        val element = usageInfo.element!!
        if (parent == null) return KotlinSliceUsage(element, params)
        return KotlinSliceUsage(element, parent, lambdaLevel, forcedExpressionMode)
    }

    override fun getUsageInfo(): UsageInfo {
        val originalInfo = super.getUsageInfo()
        if (lambdaLevel > 0 && forcedExpressionMode) {
            val element = originalInfo.element ?: return originalInfo
            // Do not let IDEA consider usages of the same anonymous function as duplicates when their levels differ
            return UsageInfoLambdaWrapper(element)
        }
        return originalInfo
    }

    override fun canBeLeaf() = element != null && lambdaLevel == 0

    public override fun processUsagesFlownDownTo(element: PsiElement, uniqueProcessor: Processor<SliceUsage>) {
        InflowSlicer(element as? KtExpression ?: return, uniqueProcessor, this).processChildren()
    }

    public override fun processUsagesFlownFromThe(element: PsiElement, uniqueProcessor: Processor<SliceUsage>) {
        OutflowSlicer(element as? KtExpression ?: return, uniqueProcessor, this).processChildren()
    }
}