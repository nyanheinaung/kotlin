/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.j2k

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.j2k.*

class OldJ2kConverterExtension : J2kConverterExtension() {
    override val isNewJ2k = false

    override fun createJavaToKotlinConverter(
        project: Project,
        settings: ConverterSettings,
        services: JavaToKotlinConverterServices
    ): JavaToKotlinConverter =
        OldJavaToKotlinConverter(project, settings, services)

    override fun createPostProcessor(formatCode: Boolean, settings: ConverterSettings): PostProcessor =
        J2kPostProcessor(formatCode)
}