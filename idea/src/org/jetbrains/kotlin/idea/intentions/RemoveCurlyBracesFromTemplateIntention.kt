/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.openapi.editor.Editor
import org.jetbrains.kotlin.idea.core.canDropBraces
import org.jetbrains.kotlin.idea.core.dropBraces
import org.jetbrains.kotlin.idea.inspections.IntentionBasedInspection
import org.jetbrains.kotlin.psi.KtBlockStringTemplateEntry

class RemoveCurlyBracesFromTemplateInspection :
    IntentionBasedInspection<KtBlockStringTemplateEntry>(RemoveCurlyBracesFromTemplateIntention::class)

class RemoveCurlyBracesFromTemplateIntention :
    SelfTargetingOffsetIndependentIntention<KtBlockStringTemplateEntry>(KtBlockStringTemplateEntry::class.java, "Remove curly braces") {
    override fun isApplicableTo(element: KtBlockStringTemplateEntry) = element.canDropBraces()

    override fun applyTo(element: KtBlockStringTemplateEntry, editor: Editor?) {
        element.dropBraces()
    }
}
