/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl

import org.jetbrains.kotlin.psi.KtModifierList

import org.jetbrains.kotlin.lexer.KtTokens.MODIFIER_KEYWORDS_ARRAY
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken

object ModifierMaskUtils {
    init {
        assert(MODIFIER_KEYWORDS_ARRAY.size <= 32) { "Current implementation depends on the ability to represent modifier list as bit mask" }
    }

    @JvmStatic
    fun computeMaskFromModifierList(modifierList: KtModifierList): Int = computeMask { modifierList.hasModifier(it) }

    @JvmStatic
    fun computeMask(hasModifier: (KtModifierKeywordToken) -> Boolean): Int {
        var mask = 0
        for ((index, modifierKeywordToken) in MODIFIER_KEYWORDS_ARRAY.withIndex()) {
            if (hasModifier(modifierKeywordToken)) {
                mask = mask or (1 shl index)
            }
        }
        return mask
    }

    @JvmStatic
    fun maskHasModifier(mask: Int, modifierToken: KtModifierKeywordToken): Boolean {
        val index = MODIFIER_KEYWORDS_ARRAY.indexOf(modifierToken)
        assert(index >= 0) { "All JetModifierKeywordTokens should be present in MODIFIER_KEYWORDS_ARRAY" }
        return (mask and (1 shl index)) != 0
    }

    @JvmStatic
    fun maskToString(mask: Int): String {
        val sb = StringBuilder()
        sb.append("[")
        var first = true
        for (modifierKeyword in MODIFIER_KEYWORDS_ARRAY) {
            if (maskHasModifier(mask, modifierKeyword)) {
                if (!first) {
                    sb.append(" ")
                }
                sb.append(modifierKeyword.value)
                first = false
            }
        }
        sb.append("]")
        return sb.toString()
    }

}
