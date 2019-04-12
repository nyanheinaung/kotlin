/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.testFramework

import com.intellij.mock.MockComponentManager
import com.intellij.openapi.application.Application
import com.intellij.util.containers.ContainerUtil
import org.jetbrains.kotlin.test.testFramework.KtUsefulTestCase

object MockComponentManagerCreationTracer {

    private val creationTraceMap = ContainerUtil.createConcurrentWeakMap<MockComponentManager, Throwable>()

    @JvmStatic
    fun onCreate(manager: MockComponentManager) {
        creationTraceMap[manager] = Exception("Creation trace")
    }

    @JvmStatic
    fun onGetComponentInstance(manager: MockComponentManager) {
        if (manager.isDisposed) {
            val trace = creationTraceMap[manager] ?: return
            trace.printStackTrace(System.err)
        }
    }

    @JvmStatic
    fun diagnoseDisposedButNotClearedApplication(app: Application) {
        if (app is MockComponentManager) {
            KtUsefulTestCase.resetApplicationToNull()
            throw IllegalStateException("Some test disposed, but forgot to clear MockApplication", creationTraceMap[app])
        }
    }
}