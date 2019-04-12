/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.kotlin

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.serialization.deserialization.KotlinMetadataFinder

interface MetadataFinderFactory {
    fun create(scope: GlobalSearchScope): KotlinMetadataFinder
    fun create(project: Project, module: ModuleDescriptor): KotlinMetadataFinder
}
