/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.synthetic.res

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.android.synthetic.AndroidXmlHandler
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import java.io.ByteArrayInputStream
import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory

class CliAndroidLayoutXmlFileManager(
        project: Project,
        applicationPackage: String,
        variants: List<AndroidVariant>
) : AndroidLayoutXmlFileManager(project) {
    override val androidModule = AndroidModule(applicationPackage, variants)

    private val saxParser: SAXParser = initSAX()

    override fun doExtractResources(layoutGroup: AndroidLayoutGroupData, module: ModuleDescriptor): AndroidLayoutGroup {
        val layouts = layoutGroup.layouts.map { layout ->
            val resources = arrayListOf<AndroidResource>()

            val inputStream = ByteArrayInputStream(layout.virtualFile.contentsToByteArray())
            saxParser.parse(inputStream, AndroidXmlHandler { id, tag ->
                resources += parseAndroidResource(id, tag, null)
            })

            AndroidLayout(resources)
        }

        return AndroidLayoutGroup(layoutGroup.name, layouts)
    }

    private fun initSAX(): SAXParser {
        val saxFactory = SAXParserFactory.newInstance()
        saxFactory.isNamespaceAware = true
        return saxFactory.newSAXParser()
    }

}
