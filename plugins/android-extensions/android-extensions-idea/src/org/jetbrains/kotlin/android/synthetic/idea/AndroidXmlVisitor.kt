/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.synthetic.idea

import com.intellij.psi.PsiElement
import com.intellij.psi.XmlElementVisitor
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlElement
import com.intellij.psi.xml.XmlTag
import org.jetbrains.kotlin.android.synthetic.AndroidConst
import org.jetbrains.kotlin.android.synthetic.androidIdToName
import org.jetbrains.kotlin.android.synthetic.isWidgetTypeIgnored
import org.jetbrains.kotlin.android.synthetic.res.ResourceIdentifier

class AndroidXmlVisitor(val elementCallback: (ResourceIdentifier, String, XmlAttribute) -> Unit) : XmlElementVisitor() {

    override fun visitElement(element: PsiElement) {
        element.acceptChildren(this)
    }

    override fun visitXmlElement(element: XmlElement?) {
        element?.acceptChildren(this)
    }

    override fun visitXmlTag(tag: XmlTag?) {
        val localName = tag?.localName ?: ""
        if (isWidgetTypeIgnored(localName)) {
            tag?.acceptChildren(this)
            return
        }

        val idAttribute = tag?.getAttribute(AndroidConst.ID_ATTRIBUTE_NO_NAMESPACE, AndroidConst.ANDROID_NAMESPACE)
        if (idAttribute != null) {
            val idAttributeValue = idAttribute.value
            if (idAttributeValue != null) {
                val xmlType = tag.getAttribute(AndroidConst.CLASS_ATTRIBUTE_NO_NAMESPACE)?.value ?: localName
                val name = androidIdToName(idAttributeValue)
                if (name != null) elementCallback(name, xmlType, idAttribute)
            }
        }

        tag?.acceptChildren(this)
    }
}