/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project

interface ProbablyNothingCallableNames {
    fun functionNames(): Collection<String>
    fun propertyNames(): Collection<String>

    companion object {
        fun getInstance(project: Project) = ServiceManager.getService(project, ProbablyNothingCallableNames::class.java)!!
    }
}
