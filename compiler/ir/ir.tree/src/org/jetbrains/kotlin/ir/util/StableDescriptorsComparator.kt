/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.util

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.renderer.ClassifierNamePolicy
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import org.jetbrains.kotlin.renderer.DescriptorRendererModifier
import org.jetbrains.kotlin.renderer.OverrideRenderingPolicy
import java.util.*

object StableDescriptorsComparator : Comparator<DeclarationDescriptor> {
    override fun compare(member1: DeclarationDescriptor?, member2: DeclarationDescriptor?): Int {
        if (member1 == member2) return 0
        if (member1 == null) return -1
        if (member2 == null) return 1

        val image1 = DESCRIPTOR_RENDERER.render(member1)
        val image2 = DESCRIPTOR_RENDERER.render(member2)
        return image1.compareTo(image2)
    }

    private val DESCRIPTOR_RENDERER = DescriptorRenderer.withOptions {
        withDefinedIn = false
        overrideRenderingPolicy = OverrideRenderingPolicy.RENDER_OPEN_OVERRIDE
        includePropertyConstant = true
        classifierNamePolicy = ClassifierNamePolicy.FULLY_QUALIFIED
        verbose = true
        modifiers = DescriptorRendererModifier.ALL
    }
}