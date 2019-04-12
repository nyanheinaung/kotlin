/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.inspection

import com.android.resources.ResourceFolderType
import com.android.resources.ResourceType
import com.intellij.codeInsight.daemon.QuickFixActionRegistrar
import com.intellij.codeInsight.quickfix.UnresolvedReferenceQuickFixProvider
import com.intellij.openapi.module.ModuleUtil
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.android.inspections.CreateFileResourceQuickFix
import org.jetbrains.android.inspections.CreateValueResourceQuickFix
import org.jetbrains.android.util.AndroidResourceUtil
import org.jetbrains.kotlin.android.getReferredResourceOrManifestField
import org.jetbrains.kotlin.idea.references.KtSimpleNameReference


class KotlinAndroidResourceQuickFixProvider : UnresolvedReferenceQuickFixProvider<KtSimpleNameReference>() {

    override fun registerFixes(ref: KtSimpleNameReference, registrar: QuickFixActionRegistrar) {
        val expression = ref.expression
        val contextModule = ModuleUtil.findModuleForPsiElement(expression) ?: return
        val facet = AndroidFacet.getInstance(contextModule) ?: return
        val manifest = facet.manifest ?: return
        manifest.`package`.value ?: return
        val contextFile = expression.containingFile ?: return

        val info = getReferredResourceOrManifestField(facet, expression, true)
        if (info == null || info.isFromManifest) {
            return
        }

        val resourceType = ResourceType.getEnum(info.className)

        if (AndroidResourceUtil.ALL_VALUE_RESOURCE_TYPES.contains(resourceType)) {
            registrar.register(CreateValueResourceQuickFix(facet, resourceType, info.fieldName, contextFile, true))
        }
        if (AndroidResourceUtil.XML_FILE_RESOURCE_TYPES.contains(resourceType)) {
            val resourceFolderType = ResourceFolderType.getTypeByName(resourceType.getName());
            if (resourceFolderType != null) {
                registrar.register(CreateFileResourceQuickFix(facet, resourceFolderType, info.fieldName, contextFile, true))
            }
        }
    }

    override fun getReferenceClass() = KtSimpleNameReference::class.java
}

