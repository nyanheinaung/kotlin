/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.codeStyle.CodeStyleManager
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.refactoring.withExpectedActuals
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.siblings
import org.jetbrains.kotlin.psi.psiUtil.startOffset

class ConvertEnumToSealedClassIntention : SelfTargetingRangeIntention<KtClass>(KtClass::class.java, "Convert to sealed class") {
    override fun applicabilityRange(element: KtClass): TextRange? {
        val nameIdentifier = element.nameIdentifier ?: return null
        val enumKeyword = element.modifierList?.getModifier(KtTokens.ENUM_KEYWORD) ?: return null
        return TextRange(enumKeyword.startOffset, nameIdentifier.endOffset)
    }

    override fun applyTo(element: KtClass, editor: Editor?) {
        val name = element.name ?: return
        if (name.isEmpty()) return

        for (klass in element.withExpectedActuals()) {
            klass as? KtClass ?: continue

            val classDescriptor = klass.resolveToDescriptorIfAny() as? ClassDescriptor ?: continue
            val isExpect = classDescriptor.isExpect
            val isActual = classDescriptor.isActual

            klass.removeModifier(KtTokens.ENUM_KEYWORD)
            klass.addModifier(KtTokens.SEALED_KEYWORD)

            val psiFactory = KtPsiFactory(klass)

            for (member in klass.declarations) {
                if (member !is KtEnumEntry) continue

                val obj = psiFactory.createDeclaration<KtObjectDeclaration>("object ${member.name}")

                val initializers = member.initializerList?.initializers ?: emptyList()
                if (initializers.isNotEmpty()) {
                    initializers.forEach { obj.addSuperTypeListEntry(psiFactory.createSuperTypeCallEntry("${klass.name}${it.text}")) }
                }
                else {
                    val defaultEntry = if (isExpect) psiFactory.createSuperTypeEntry(name) else psiFactory.createSuperTypeCallEntry("$name()")
                    obj.addSuperTypeListEntry(defaultEntry)
                }

                if (isActual) {
                    obj.addModifier(KtTokens.ACTUAL_KEYWORD)
                }

                member.getBody()?.let { body -> obj.add(body) }

                member.delete()
                klass.addDeclaration(obj)
            }

            klass.getBody()?.let { body ->
                val semicolon = body
                        .allChildren
                        .takeWhile { it !is KtDeclaration }
                        .firstOrNull { it.node.elementType == KtTokens.SEMICOLON }
                if (semicolon != null) {
                    val nonWhiteSibling = semicolon.siblings(forward = true, withItself = false).firstOrNull { it !is PsiWhiteSpace }
                    body.deleteChildRange(semicolon, nonWhiteSibling?.prevSibling ?: semicolon)
                    if (nonWhiteSibling != null) {
                        CodeStyleManager.getInstance(klass.project).reformat(nonWhiteSibling.firstChild ?: nonWhiteSibling)
                    }
                }
            }
        }
    }
}
