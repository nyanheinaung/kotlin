/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.checkers

import org.jetbrains.kotlin.container.DefaultImplementation
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor

@DefaultImplementation(PlatformDiagnosticSuppressor.Default::class)
interface PlatformDiagnosticSuppressor {
    fun shouldReportUnusedParameter(parameter: VariableDescriptor): Boolean

    fun shouldReportNoBody(descriptor: CallableMemberDescriptor): Boolean

    object Default : PlatformDiagnosticSuppressor {
        override fun shouldReportUnusedParameter(parameter: VariableDescriptor): Boolean = true

        override fun shouldReportNoBody(descriptor: CallableMemberDescriptor): Boolean = true
    }
}
