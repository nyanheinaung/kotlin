/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.uast.*
import org.jetbrains.uast.kotlin.declarations.KotlinUIdentifier
import org.jetbrains.uast.kotlin.kinds.KotlinSpecialExpressionKinds

class KotlinUSwitchExpression(
        override val psi: KtWhenExpression,
        givenParent: UElement?
) : KotlinAbstractUExpression(givenParent), USwitchExpression, KotlinUElementWithType {
    override val expression by lz { KotlinConverter.convertOrNull(psi.subjectExpression, this) }

    override val body: UExpressionList by lz {
        object : KotlinUExpressionList(psi, KotlinSpecialExpressionKinds.WHEN, this@KotlinUSwitchExpression) {
            override fun asRenderString() = expressions.joinToString("\n") { it.asRenderString().withMargin }
        }.apply {
            expressions = this@KotlinUSwitchExpression.psi.entries.map { KotlinUSwitchEntry(it, this) }
        }
    }

    override fun asRenderString() = buildString {
        val expr = expression?.let { "(" + it.asRenderString() + ") " } ?: ""
        appendln("switch $expr {")
        appendln(body.asRenderString())
        appendln("}")
    }

    override val switchIdentifier: UIdentifier
        get() = KotlinUIdentifier(null, this)
}

class KotlinUSwitchEntry(
        override val psi: KtWhenEntry,
        givenParent: UElement?
) : KotlinAbstractUExpression(givenParent), USwitchClauseExpressionWithBody {
    override val caseValues by lz {
        psi.conditions.map { KotlinConverter.convertWhenCondition(it, this) ?: UastEmptyExpression }
    }

    override val body: UExpressionList by lz {
        object : KotlinUExpressionList(psi, KotlinSpecialExpressionKinds.WHEN_ENTRY, this@KotlinUSwitchEntry) {
            override fun asRenderString() = buildString {
                appendln("{")
                expressions.forEach { appendln(it.asRenderString().withMargin) }
                appendln("}")
            }
        }.apply {
            val exprPsi = this@KotlinUSwitchEntry.psi.expression
            val userExpressions = when (exprPsi) {
                is KtBlockExpression -> exprPsi.statements.map { KotlinConverter.convertOrEmpty(it, this) }
                else -> listOf(KotlinConverter.convertOrEmpty(exprPsi, this))
            }
            expressions = userExpressions + object : UBreakExpression, JvmDeclarationUElementPlaceholder {
                override val javaPsi: PsiElement? = null
                override val sourcePsi: PsiElement? = null
                override val psi: PsiElement?
                    get() = null
                override val label: String?
                    get() = null
                override val uastParent: UElement?
                    get() = this@KotlinUSwitchEntry
                override val annotations: List<UAnnotation>
                    get() = emptyList()
            }
        }
    }

    override fun convertParent(): UElement? {
        val result = KotlinConverter.unwrapElements(psi.parent)?.let { parentUnwrapped ->
            KotlinUastLanguagePlugin().convertElementWithParent(parentUnwrapped, null)
        }
        return (result as? KotlinUSwitchExpression)?.body ?: result
    }
}