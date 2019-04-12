/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.checkers;

import com.intellij.psi.PsiElement;
import kotlin.collections.CollectionsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.builtins.KotlinBuiltIns;
import org.jetbrains.kotlin.descriptors.ClassDescriptor;
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor;
import org.jetbrains.kotlin.diagnostics.Errors;
import org.jetbrains.kotlin.psi.KtTypeProjection;
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall;
import org.jetbrains.kotlin.types.KotlinType;
import org.jetbrains.kotlin.types.typeUtil.TypeUtilsKt;

import java.util.Map;

public class ReifiedTypeParameterSubstitutionChecker implements CallChecker {
    @Override
    public void check(@NotNull ResolvedCall<?> resolvedCall, @NotNull PsiElement reportOn, @NotNull CallCheckerContext context) {
        Map<TypeParameterDescriptor, KotlinType> typeArguments = resolvedCall.getTypeArguments();
        for (Map.Entry<TypeParameterDescriptor, KotlinType> entry : typeArguments.entrySet()) {
            TypeParameterDescriptor parameter = entry.getKey();
            KotlinType argument = entry.getValue();
            ClassifierDescriptor argumentDeclarationDescriptor = argument.getConstructor().getDeclarationDescriptor();

            if (!parameter.isReified() && !isTypeParameterOfKotlinArray(parameter)) {
                continue;
            }

            KtTypeProjection typeProjection = CollectionsKt.getOrNull(resolvedCall.getCall().getTypeArguments(), parameter.getIndex());
            PsiElement reportErrorOn = typeProjection != null ? typeProjection : reportOn;

            if (argumentDeclarationDescriptor instanceof TypeParameterDescriptor &&
                !((TypeParameterDescriptor) argumentDeclarationDescriptor).isReified()) {
                context.getTrace().report(Errors.TYPE_PARAMETER_AS_REIFIED.on(reportErrorOn, (TypeParameterDescriptor) argumentDeclarationDescriptor));
            }
            else if (TypeUtilsKt.cannotBeReified(argument)) {
                context.getTrace().report(Errors.REIFIED_TYPE_FORBIDDEN_SUBSTITUTION.on(reportErrorOn, argument));
            }
            // REIFIED_TYPE_UNSAFE_SUBSTITUTION is temporary disabled because it seems too strict now (see KT-10847)
            //else if (TypeUtilsKt.unsafeAsReifiedArgument(argument) && !hasPureReifiableAnnotation(parameter)) {
            //    context.getTrace().report(Errors.REIFIED_TYPE_UNSAFE_SUBSTITUTION.on(reportErrorOn, argument));
            //}
        }
    }

    /*
    private static final FqName PURE_REIFIABLE_ANNOTATION_FQ_NAME = new FqName("kotlin.internal.PureReifiable");

    private static boolean hasPureReifiableAnnotation(@NotNull TypeParameterDescriptor parameter) {
        return parameter.getAnnotations().hasAnnotation(PURE_REIFIABLE_ANNOTATION_FQ_NAME) ||
               isTypeParameterOfKotlinArray(parameter);
    }
    */

    private static boolean isTypeParameterOfKotlinArray(@NotNull TypeParameterDescriptor parameter) {
        DeclarationDescriptor container = parameter.getContainingDeclaration();
        return container instanceof ClassDescriptor && KotlinBuiltIns.isNonPrimitiveArray((ClassDescriptor) container);
    }
}
