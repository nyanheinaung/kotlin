/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.configuration

import com.intellij.openapi.module.Module
import org.jetbrains.kotlin.idea.actions.NewKotlinFileHook
import org.jetbrains.kotlin.psi.KtFile

class NewKotlinFileConfigurationHook : NewKotlinFileHook() {
    override fun postProcess(createdElement: KtFile, module: Module) {
        showConfigureKotlinNotificationIfNeeded(module)
    }
}