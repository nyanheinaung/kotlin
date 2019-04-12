/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.modules

import java.io.Serializable

data class TargetId(val name: String, val type: String) : Serializable

fun TargetId(module: Module): TargetId =
        TargetId(module.getModuleName(), module.getModuleType())