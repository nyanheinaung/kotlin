/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.scopes.receivers.ExpressionReceiver
import org.jetbrains.kotlin.util.slicedMap.BasicWritableSlice
import org.jetbrains.kotlin.util.slicedMap.RewritePolicy
import org.jetbrains.kotlin.util.slicedMap.Slices
import org.jetbrains.kotlin.util.slicedMap.WritableSlice

object JvmBindingContextSlices {
    @JvmField
    val RUNTIME_ASSERTION_INFO: WritableSlice<KtExpression, RuntimeAssertionInfo> = BasicWritableSlice(RewritePolicy.DO_NOTHING)

    @JvmField
    val RECEIVER_RUNTIME_ASSERTION_INFO: WritableSlice<ExpressionReceiver, RuntimeAssertionInfo> = BasicWritableSlice(RewritePolicy.DO_NOTHING)

    @JvmField
    val BODY_RUNTIME_ASSERTION_INFO: WritableSlice<KtExpression, RuntimeAssertionInfo> = BasicWritableSlice(RewritePolicy.DO_NOTHING)

    @JvmField
    val LOAD_FROM_JAVA_SIGNATURE_ERRORS: WritableSlice<DeclarationDescriptor, List<String>> = Slices.createCollectiveSlice()

    init {
        BasicWritableSlice.initSliceDebugNames(JvmBindingContextSlices::class.java)
    }
}
