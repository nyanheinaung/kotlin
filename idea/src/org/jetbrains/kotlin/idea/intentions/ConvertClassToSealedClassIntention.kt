/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.lexer.KtTokens.*
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset

class ConvertClassToSealedClassIntention : SelfTargetingRangeIntention<KtClass>(KtClass::class.java, "Convert to sealed class") {

    override fun applicabilityRange(element: KtClass): TextRange? {
        if (element.modifierList == null) return null
        if (!element.hasModifier(OPEN_KEYWORD) && !element.hasModifier(ABSTRACT_KEYWORD)) return null

        val constructors = listOfNotNull(element.primaryConstructor) + element.secondaryConstructors
        if (constructors.isEmpty()) return null
        if (!constructors.all { it.hasModifier(PRIVATE_KEYWORD) && it.getAnnotationEntries().isEmpty() }) return null

        val nameIdentifier = element.nameIdentifier ?: return null
        return TextRange(element.startOffset, nameIdentifier.endOffset)
    }

    override fun applyTo(element: KtClass, editor: Editor?) {
        element.modifierList?.run {
            getModifier(OPEN_KEYWORD)?.delete()
            getModifier(ABSTRACT_KEYWORD)?.delete()
        }
        element.addModifier(SEALED_KEYWORD)

        element.primaryConstructor?.run {
            if (element.secondaryConstructors.isEmpty() && valueParameters.isEmpty()) {
                this.delete()
            }
            else {
                val newConstructor = this.copy() as KtPrimaryConstructor
                newConstructor.modifierList?.getModifier(PRIVATE_KEYWORD)?.delete()
                newConstructor.getConstructorKeyword()?.delete()
                this.replace(newConstructor)
            }
        }

        element.secondaryConstructors.forEach {
            it.modifierList?.getModifier(PRIVATE_KEYWORD)?.delete()
        }
    }

}
