/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.debugger.breakpoints.dialog

import com.intellij.psi.PsiClass
import org.jetbrains.kotlin.asJava.classes.KtLightClass
import org.jetbrains.kotlin.asJava.classes.KtLightClassForFacade
import org.jetbrains.kotlin.idea.caches.resolve.unsafeResolveToDescriptor
import org.jetbrains.kotlin.idea.core.util.DescriptorMemberChooserObject
import org.jetbrains.kotlin.psi.KtProperty

fun PsiClass.collectProperties(): Array<DescriptorMemberChooserObject> {
    if (this is KtLightClassForFacade) {
        val result = arrayListOf<DescriptorMemberChooserObject>()
        this.files.forEach {
            it.declarations.filterIsInstance<KtProperty>().forEach {
                result.add(DescriptorMemberChooserObject(it, it.unsafeResolveToDescriptor()))
            }
        }
        return result.toTypedArray()
    }
    if (this is KtLightClass) {
        val origin = this.kotlinOrigin
        if (origin != null) {
            return origin.declarations.filterIsInstance<KtProperty>().map {
                DescriptorMemberChooserObject(it, it.unsafeResolveToDescriptor())
            }.toTypedArray()
        }
    }
    return emptyArray()
}

