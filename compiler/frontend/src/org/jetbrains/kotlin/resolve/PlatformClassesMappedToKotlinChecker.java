/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.builtins.PlatformToKotlinClassMap;
import org.jetbrains.kotlin.descriptors.ClassDescriptor;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.psi.KtElement;
import org.jetbrains.kotlin.psi.KtExpression;
import org.jetbrains.kotlin.psi.KtImportDirective;

import java.util.Collection;

import static org.jetbrains.kotlin.diagnostics.Errors.PLATFORM_CLASS_MAPPED_TO_KOTLIN;

public class PlatformClassesMappedToKotlinChecker {
    public static void checkPlatformClassesMappedToKotlin(
            @NotNull PlatformToKotlinClassMap platformToKotlinMap,
            @NotNull BindingTrace trace,
            @NotNull KtImportDirective importDirective,
            @NotNull Collection<? extends DeclarationDescriptor> descriptors
    ) {
        KtExpression importedReference = importDirective.getImportedReference();
        if (importedReference != null) {
            for (DeclarationDescriptor descriptor : descriptors) {
                reportPlatformClassMappedToKotlin(platformToKotlinMap, trace, importedReference, descriptor);
            }
        }
    }

    public static void reportPlatformClassMappedToKotlin(
            @NotNull PlatformToKotlinClassMap platformToKotlinMap,
            @NotNull BindingTrace trace,
            @NotNull KtElement element,
            @NotNull DeclarationDescriptor descriptor
    ) {
        if (!(descriptor instanceof ClassDescriptor)) return;

        Collection<ClassDescriptor> kotlinAnalogsForClass = platformToKotlinMap.mapPlatformClass((ClassDescriptor) descriptor);
        if (!kotlinAnalogsForClass.isEmpty()) {
            trace.report(PLATFORM_CLASS_MAPPED_TO_KOTLIN.on(element, kotlinAnalogsForClass));
        }
    }
}
