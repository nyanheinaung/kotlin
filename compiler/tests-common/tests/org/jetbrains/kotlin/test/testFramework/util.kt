/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.test.testFramework

import com.intellij.openapi.application.ApplicationManager

fun <T> runWriteAction(action: () -> T): T {
    return ApplicationManager.getApplication().runWriteAction<T>(action)
}