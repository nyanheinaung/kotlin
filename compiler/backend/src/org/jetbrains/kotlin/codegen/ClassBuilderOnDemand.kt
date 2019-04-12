/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.storage.LockBasedStorageManager

class ClassBuilderOnDemand(createClassBuilder: () -> ClassBuilder) : DelegatingClassBuilder() {
    private val classBuilder = LockBasedStorageManager.NO_LOCKS.createLazyValue(createClassBuilder)

    val isComputed: Boolean
        get() = classBuilder.isComputed()

    override fun getDelegate() = classBuilder()

    fun ensureGenerated() {
        classBuilder()
    }

    override fun done() {
        if (isComputed) {
            classBuilder().done()
        }
    }
}
