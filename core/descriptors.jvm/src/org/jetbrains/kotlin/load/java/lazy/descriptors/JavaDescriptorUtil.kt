/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.lazy.descriptors

import org.jetbrains.kotlin.descriptors.PropertyDescriptor

// Currently getter is null iff it's loaded from Java field
val PropertyDescriptor.isJavaField: Boolean
    get() = getter == null
