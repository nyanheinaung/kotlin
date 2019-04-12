/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.configuration

import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.ui.FileTypeBasedRootFilter
import org.jetbrains.kotlin.idea.KotlinFileType

class KotlinSourceRootDetector : FileTypeBasedRootFilter(OrderRootType.SOURCES, false, KotlinFileType.INSTANCE, "sources")
