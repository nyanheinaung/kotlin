/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve

import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.UnwrappedType
import org.jetbrains.kotlin.types.WrappedTypeFactory

interface DeclarationReturnTypeSanitizer {
    fun sanitizeReturnType(
        inferred: UnwrappedType,
        wrappedTypeFactory: WrappedTypeFactory,
        trace: BindingTrace,
        languageVersionSettings: LanguageVersionSettings
    ): UnwrappedType

    object Default : DeclarationReturnTypeSanitizer {
        override fun sanitizeReturnType(
            inferred: UnwrappedType,
            wrappedTypeFactory: WrappedTypeFactory,
            trace: BindingTrace,
            languageVersionSettings: LanguageVersionSettings
        ) = inferred
    }
}