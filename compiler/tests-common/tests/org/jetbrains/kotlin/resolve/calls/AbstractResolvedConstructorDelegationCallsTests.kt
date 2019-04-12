/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getParentResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall


abstract class AbstractResolvedConstructorDelegationCallsTests : AbstractResolvedCallsTest() {
    override fun buildCachedCallAtIndex(
            bindingContext: BindingContext, jetFile: KtFile, index: Int
    ): Pair<PsiElement?, ResolvedCall<out CallableDescriptor>?> {
        val element = jetFile.findElementAt(index)
        val constructor = element?.getNonStrictParentOfType<KtSecondaryConstructor>()!!
        val delegationCall = constructor.getDelegationCall()

        val cachedCall = delegationCall.getParentResolvedCall(bindingContext, strict = false)
        return Pair(delegationCall, cachedCall)
    }
}
