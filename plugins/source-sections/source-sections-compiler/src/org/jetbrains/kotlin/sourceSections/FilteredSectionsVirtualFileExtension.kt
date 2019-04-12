/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.sourceSections

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.extensions.PreprocessedVirtualFileFactoryExtension

class FilteredSectionsVirtualFileExtension(val allowedSections: Set<String>?) : PreprocessedVirtualFileFactoryExtension {

    override fun isPassThrough(): Boolean = allowedSections == null || allowedSections.isEmpty()

    override fun createPreprocessedFile(file: VirtualFile?): VirtualFile? =
            when {
                file == null || allowedSections == null || allowedSections.isEmpty() -> file
                file is LightVirtualFile -> FilteredSectionsLightVirtualFile(file, allowedSections)
                else -> FilteredSectionsVirtualFile(file, allowedSections)
            }

    override fun createPreprocessedLightFile(file: LightVirtualFile?): LightVirtualFile? =
            when {
                file == null || allowedSections == null || allowedSections.isEmpty() -> file
                else -> FilteredSectionsLightVirtualFile(file, allowedSections)
            }
}