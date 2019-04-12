/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.synthetic

import org.jetbrains.kotlin.android.synthetic.res.ResourceIdentifier
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.util.HashMap

class AndroidXmlHandler(private val elementCallback: (ResourceIdentifier, String) -> Unit) : DefaultHandler() {
    override fun startDocument() {
        super.startDocument()
    }

    override fun endDocument() {
        super.endDocument()
    }

    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        if (isWidgetTypeIgnored(localName)) return
        val attributesMap = attributes.toMap()
        val idAttribute = attributesMap[AndroidConst.ID_ATTRIBUTE_NO_NAMESPACE]
        val widgetType = attributesMap[AndroidConst.CLASS_ATTRIBUTE_NO_NAMESPACE] ?: localName
        val name = idAttribute?.let { androidIdToName(idAttribute) }
        if (name != null) elementCallback(name, widgetType)
    }

    override fun endElement(uri: String?, localName: String, qName: String) {}
}

private fun Attributes.toMap(): HashMap<String, String> {
    val res = HashMap<String, String>()
    for (index in 0..length - 1) {
        val attrName = getLocalName(index)!!
        val attrVal = getValue(index)!!
        res[attrName] = attrVal
    }
    return res
}


