/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.config

import com.intellij.openapi.util.Key
import java.io.File

val APPEND_JAVA_SOURCE_ROOTS_HANDLER_KEY = Key<(List<File>) -> Unit>("AppendJavaSourceRootsHandlerKey")