/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm

import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.annotations.FilteredAnnotations
import org.jetbrains.kotlin.load.java.JvmAnnotationNames
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.DeclarationReturnTypeSanitizer
import org.jetbrains.kotlin.types.UnwrappedType
import org.jetbrains.kotlin.types.WrappedTypeFactory

object JvmDeclarationReturnTypeSanitizer : DeclarationReturnTypeSanitizer {
    override fun sanitizeReturnType(
            inferred: UnwrappedType,
            wrappedTypeFactory: WrappedTypeFactory,
            trace: BindingTrace,
            languageVersionSettings: LanguageVersionSettings
    ): UnwrappedType =
            if (languageVersionSettings.supportsFeature(LanguageFeature.StrictJavaNullabilityAssertions)) {
                // NB can't check for presence of EnhancedNullability here,
                // because it will also cause recursion in declaration type resolution.
                inferred.replaceAnnotations(FilteredAnnotations(inferred.annotations) {
                    it != JvmAnnotationNames.ENHANCED_NULLABILITY_ANNOTATION
                })
            }
            else inferred
}