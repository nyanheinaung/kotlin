/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.synthetic.idea

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlFile
import org.jetbrains.kotlin.android.model.AndroidModuleInfoProvider
import org.jetbrains.kotlin.android.synthetic.AndroidConst
import org.jetbrains.kotlin.android.synthetic.androidIdToName
import org.jetbrains.kotlin.idea.references.KtSimpleNameReference
import org.jetbrains.kotlin.plugin.references.SimpleNameReferenceExtension
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtPsiFactory

class AndroidSimpleNameReferenceExtension : SimpleNameReferenceExtension {

    override fun isReferenceTo(reference: KtSimpleNameReference, element: PsiElement): Boolean =
            element is XmlFile && reference.isReferenceToXmlFile(element)

    private fun isLayoutPackageIdentifier(reference: KtSimpleNameReference): Boolean {
        val probablyVariant = reference.element?.parent as? KtDotQualifiedExpression ?: return false
        val probablyKAS = probablyVariant.receiverExpression as? KtDotQualifiedExpression ?: return false
        return probablyKAS.receiverExpression.text == AndroidConst.SYNTHETIC_PACKAGE
    }

    override fun handleElementRename(reference: KtSimpleNameReference, psiFactory: KtPsiFactory, newElementName: String): PsiElement? {
        val resolvedElement = reference.resolve()
        if (resolvedElement is XmlAttributeValue && isIdDeclaration(resolvedElement)) {
            val newSyntheticPropertyName = androidIdToName(newElementName) ?: return null
            return psiFactory.createNameIdentifier(newSyntheticPropertyName.name)
        }
        else if (isLayoutPackageIdentifier(reference)) {
            return psiFactory.createSimpleName(newElementName.removeSuffix(".xml")).getIdentifier()
        }

        return null
    }

    private fun isIdDeclaration(declaration: XmlAttributeValue) = declaration.value?.startsWith("@+id/") ?: false

    private fun KtSimpleNameReference.isReferenceToXmlFile(xmlFile: XmlFile): Boolean {
        if (!isLayoutPackageIdentifier(this)) {
            return false
        }

        if (xmlFile.name.removeSuffix(".xml") != element.getReferencedName()) {
            return false
        }

        val virtualFile = xmlFile.virtualFile ?: return false
        val layoutDir = virtualFile.parent
        if (layoutDir.name != "layout" && !layoutDir.name.startsWith("layout-")) {
            return false
        }

        val resourceDirectories = AndroidModuleInfoProvider.getInstance(element)?.getAllResourceDirectories() ?: return false
        val resourceDirectory = virtualFile.parent?.parent ?: return false
        return resourceDirectories.any { it == resourceDirectory }
    }
}