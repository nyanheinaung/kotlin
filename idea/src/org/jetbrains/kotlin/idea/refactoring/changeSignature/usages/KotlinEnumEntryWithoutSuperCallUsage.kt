/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.refactoring.changeSignature.usages

import com.intellij.usageView.UsageInfo
import org.jetbrains.kotlin.idea.refactoring.changeSignature.KotlinChangeInfo
import org.jetbrains.kotlin.psi.*

class KotlinEnumEntryWithoutSuperCallUsage(enumEntry: KtEnumEntry) : KotlinUsageInfo<KtEnumEntry>(enumEntry) {
    override fun processUsage(changeInfo: KotlinChangeInfo, element: KtEnumEntry, allUsages: Array<out UsageInfo>): Boolean {
        if (changeInfo.newParameters.size > 0) {
            val psiFactory = KtPsiFactory(element)

            val delegatorToSuperCall = (element.addAfter(
                    psiFactory.createEnumEntryInitializerList(),
                    element.nameIdentifier
            ) as KtInitializerList).initializers[0] as KtSuperTypeCallEntry

            return KotlinFunctionCallUsage(delegatorToSuperCall, changeInfo.methodDescriptor.originalPrimaryCallable)
                    .processUsage(changeInfo, delegatorToSuperCall, allUsages)
        }

        return true
    }
}
