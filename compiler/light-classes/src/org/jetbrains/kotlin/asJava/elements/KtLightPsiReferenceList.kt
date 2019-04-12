/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.asJava.elements

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiJavaCodeReferenceElement
import com.intellij.psi.PsiReferenceList
import com.intellij.psi.PsiReferenceList.Role
import org.jetbrains.kotlin.asJava.LightClassGenerationSupport
import org.jetbrains.kotlin.asJava.classes.KtLightClass
import org.jetbrains.kotlin.asJava.classes.lazyPub
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtSuperTypeList
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.psi.psiUtil.getElementTextWithContext
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameUnsafe

class KtLightPsiReferenceList (
        override val clsDelegate: PsiReferenceList,
        private val owner: KtLightClass
) : KtLightElement<KtSuperTypeList, PsiReferenceList>, PsiReferenceList by clsDelegate {
    inner class KtLightSuperTypeReference(
            override val clsDelegate: PsiJavaCodeReferenceElement
    ) : KtLightElement<KtSuperTypeListEntry, PsiJavaCodeReferenceElement>, PsiJavaCodeReferenceElement by clsDelegate {

        override val kotlinOrigin by lazyPub {
            val superTypeList = this@KtLightPsiReferenceList.kotlinOrigin ?: return@lazyPub null
            val fqNameToFind = clsDelegate.qualifiedName ?: return@lazyPub null
            val context = LightClassGenerationSupport.getInstance(project).analyzeWithContent(superTypeList.parent as KtClassOrObject)
            superTypeList.entries.firstOrNull {
                val referencedType = context[BindingContext.TYPE, it.typeReference]
                referencedType?.constructor?.declarationDescriptor?.fqNameUnsafe?.asString() == fqNameToFind
            }
        }

        override fun getParent() = this@KtLightPsiReferenceList

        override fun delete() {
            val superTypeList = this@KtLightPsiReferenceList.kotlinOrigin ?: return
            val entry = kotlinOrigin ?: return
            superTypeList.removeEntry(entry)
        }

        override fun getTextRange(): TextRange? = kotlinOrigin?.typeReference?.textRange ?: TextRange.EMPTY_RANGE
    }

    override val kotlinOrigin: KtSuperTypeList?
        get() = owner.kotlinOrigin?.getSuperTypeList()

    private val _referenceElements by lazyPub {
        clsDelegate.referenceElements.map { KtLightSuperTypeReference(it) }.toTypedArray()
    }

    override fun getParent() = owner

    override fun getReferenceElements() = _referenceElements

    override fun add(element: PsiElement): PsiElement? {
        if (element !is KtLightSuperTypeReference) throw UnsupportedOperationException("Unexpected element: ${element.getElementTextWithContext()}")

        val superTypeList = kotlinOrigin ?: return element
        val entry = element.kotlinOrigin ?: return element
        // Only classes may be mentioned in 'extends' list, thus create super call instead simple type reference
        val entryToAdd = if ((element.parent as? PsiReferenceList)?.role == Role.IMPLEMENTS_LIST && role == Role.EXTENDS_LIST) {
            KtPsiFactory(this).createSuperTypeCallEntry("${entry.text}()")
        }
        else entry
        // TODO: implement KtSuperListEntry qualification/shortening when inserting reference from another context
        if (entry.parent != superTypeList) {
            superTypeList.addEntry(entryToAdd)
        }
        else {
            // Preserve original entry order
            entry.replace(entryToAdd)
        }
        return element
    }
}
