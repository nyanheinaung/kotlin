/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types.expressions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.builtins.KotlinBuiltIns;
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor;
import org.jetbrains.kotlin.psi.KtTypeReference;
import org.jetbrains.kotlin.resolve.BindingContext;
import org.jetbrains.kotlin.resolve.BindingTrace;
import org.jetbrains.kotlin.resolve.PossiblyBareType;
import org.jetbrains.kotlin.types.ErrorUtils;
import org.jetbrains.kotlin.types.KotlinType;
import org.jetbrains.kotlin.types.TypeConstructor;
import org.jetbrains.kotlin.types.TypeReconstructionResult;

import static org.jetbrains.kotlin.diagnostics.Errors.NO_TYPE_ARGUMENTS_ON_RHS;

public class TypeReconstructionUtil {
    @NotNull
    public static KotlinType reconstructBareType(
            @NotNull KtTypeReference right,
            @NotNull PossiblyBareType possiblyBareTarget,
            @Nullable KotlinType subjectType,
            @NotNull BindingTrace trace,
            @NotNull KotlinBuiltIns builtIns
    ) {
        if (subjectType == null) {
            // Recovery: let's reconstruct as if we were casting from Any, to get some type there
            subjectType = builtIns.getAnyType();
        }
        TypeReconstructionResult reconstructionResult = possiblyBareTarget.reconstruct(subjectType);
        if (!reconstructionResult.isAllArgumentsInferred()) {
            TypeConstructor typeConstructor = possiblyBareTarget.getBareTypeConstructor();
            trace.report(NO_TYPE_ARGUMENTS_ON_RHS.on(right,
                                                     typeConstructor.getParameters().size(),
                                                     allStarProjectionsString(typeConstructor)));
        }

        KotlinType targetType = reconstructionResult.getResultingType();
        if (targetType != null) {
            if (possiblyBareTarget.isBare()) {
                trace.record(BindingContext.TYPE, right, targetType);
            }
            return targetType;
        }

        return ErrorUtils.createErrorType("Failed to reconstruct type: " + right.getText());
    }

    @NotNull
    private static String allStarProjectionsString(@NotNull TypeConstructor constructor) {
        int size = constructor.getParameters().size();
        assert size != 0 : "No projections possible for a nilary type constructor" + constructor;
        ClassifierDescriptor declarationDescriptor = constructor.getDeclarationDescriptor();
        assert declarationDescriptor != null : "No declaration descriptor for type constructor " + constructor;
        String name = declarationDescriptor.getName().asString();

        return getTypeNameAndStarProjectionsString(name, size);
    }

    @NotNull
    public static String getTypeNameAndStarProjectionsString(@NotNull String name, int size) {
        StringBuilder builder = new StringBuilder(name);
        builder.append("<");
        for (int i = 0; i < size; i++) {
            builder.append("*");
            if (i == size - 1) break;
            builder.append(", ");
        }
        builder.append(">");

        return builder.toString();
    }
}
