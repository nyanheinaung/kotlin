/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.codeInliner

import org.jetbrains.kotlin.idea.references.ReferenceAccess
import org.jetbrains.kotlin.idea.references.readWriteAccess
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtSimpleNameExpression

class PropertyUsageReplacementStrategy(readReplacement: CodeToInline?, writeReplacement: CodeToInline?) : UsageReplacementStrategy {
    private val readReplacementStrategy = readReplacement?.let {
        CallableUsageReplacementStrategy(it, inlineSetter = false)
    }
    private val writeReplacementStrategy = writeReplacement?.let {
        CallableUsageReplacementStrategy(it, inlineSetter = true)
    }

    override fun createReplacer(usage: KtSimpleNameExpression): (() -> KtElement?)? {
        val access = usage.readWriteAccess(useResolveForReadWrite = true)
        return when (access) {
            ReferenceAccess.READ -> readReplacementStrategy?.createReplacer(usage)
            ReferenceAccess.WRITE -> writeReplacementStrategy?.createReplacer(usage)
            ReferenceAccess.READ_WRITE -> null
        }
    }
}