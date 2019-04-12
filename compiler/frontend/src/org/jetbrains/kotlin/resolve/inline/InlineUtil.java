/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.inline;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.builtins.FunctionTypesKt;
import org.jetbrains.kotlin.builtins.KotlinBuiltIns;
import org.jetbrains.kotlin.descriptors.*;
import org.jetbrains.kotlin.psi.*;
import org.jetbrains.kotlin.resolve.BindingContext;
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils;
import org.jetbrains.kotlin.resolve.calls.callUtil.CallUtilKt;
import org.jetbrains.kotlin.resolve.calls.context.ResolutionContext;
import org.jetbrains.kotlin.resolve.calls.model.ArgumentMapping;
import org.jetbrains.kotlin.resolve.calls.model.ArgumentMatch;
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall;

public class InlineUtil {

    public static boolean isInlineParameterExceptNullability(@NotNull ParameterDescriptor valueParameterOrReceiver) {
        return !(valueParameterOrReceiver instanceof ValueParameterDescriptor
                 && ((ValueParameterDescriptor) valueParameterOrReceiver).isNoinline()) &&
               FunctionTypesKt.isBuiltinFunctionalType(valueParameterOrReceiver.getOriginal().getType());
    }

    public static boolean isInlineParameter(@NotNull ParameterDescriptor valueParameterOrReceiver) {
        return isInlineParameterExceptNullability(valueParameterOrReceiver) &&
               !valueParameterOrReceiver.getOriginal().getType().isMarkedNullable();
    }

    public static boolean isInline(@Nullable DeclarationDescriptor descriptor) {
        return descriptor instanceof FunctionDescriptor && getInlineStrategy((FunctionDescriptor) descriptor).isInline();
    }

    public static boolean hasInlineAccessors(@NotNull PropertyDescriptor propertyDescriptor) {
        PropertyGetterDescriptor getter = propertyDescriptor.getGetter();
        PropertySetterDescriptor setter = propertyDescriptor.getSetter();
        return getter != null && getter.isInline() || setter != null && setter.isInline();
    }

    public static boolean isPropertyWithAllAccessorsAreInline(@NotNull DeclarationDescriptor descriptor) {
        if (!(descriptor instanceof PropertyDescriptor)) return false;

        PropertyGetterDescriptor getter = ((PropertyDescriptor) descriptor).getGetter();
        if (getter == null || !getter.isInline()) return false;

        if (((PropertyDescriptor) descriptor).isVar()) {
            PropertySetterDescriptor setter = ((PropertyDescriptor) descriptor).getSetter();
            return setter != null && setter.isInline();
        }

        return true;
    }

    public static boolean isInlineOrContainingInline(@Nullable DeclarationDescriptor descriptor) {
        if (isInline(descriptor)) return true;
        if (descriptor == null) return false;
        return isInlineOrContainingInline(descriptor.getContainingDeclaration());
    }

    @NotNull
    private static InlineStrategy getInlineStrategy(@NotNull FunctionDescriptor descriptor) {
        if (descriptor.isInline()) {
            return InlineStrategy.AS_FUNCTION;
        }

        return InlineStrategy.NOT_INLINE;
    }

    public static boolean checkNonLocalReturnUsage(
            @NotNull DeclarationDescriptor fromFunction,
            @NotNull KtExpression startExpression,
            @NotNull ResolutionContext<?> context
    ) {
        PsiElement containingFunction = context.getContextParentOfType(startExpression, KtClassOrObject.class, KtDeclarationWithBody.class);
        if (containingFunction == null) {
            return false;
        }

        return checkNonLocalReturnUsage(
                fromFunction, context.trace.get(BindingContext.DECLARATION_TO_DESCRIPTOR, containingFunction), containingFunction,
                context.trace.getBindingContext()
        );
    }

    public static boolean checkNonLocalReturnUsage(
            @NotNull DeclarationDescriptor fromFunction,
            @Nullable DeclarationDescriptor containingFunctionDescriptor,
            @Nullable PsiElement containingFunction,
            @NotNull BindingContext bindingContext
    ) {
        if (containingFunctionDescriptor == null) return false;

        while (canBeInlineArgument(containingFunction) && fromFunction != containingFunctionDescriptor) {
            if (!isInlinedArgument((KtFunction) containingFunction, bindingContext, true)) {
                return false;
            }

            containingFunctionDescriptor = getContainingClassOrFunctionDescriptor(containingFunctionDescriptor, true);

            containingFunction = containingFunctionDescriptor != null
                                 ? DescriptorToSourceUtils.descriptorToDeclaration(containingFunctionDescriptor)
                                 : null;
        }

        return fromFunction == containingFunctionDescriptor;
    }

    public static boolean isInlinedArgument(
            @NotNull KtFunction argument,
            @NotNull BindingContext bindingContext,
            boolean checkNonLocalReturn
    ) {
        ValueParameterDescriptor descriptor = getInlineArgumentDescriptor(argument, bindingContext);
        if (descriptor != null) {
            return !checkNonLocalReturn || allowsNonLocalReturns(descriptor);
        }

        return false;
    }

    @Nullable
    public static ValueParameterDescriptor getInlineArgumentDescriptor(
            @NotNull KtFunction argument,
            @NotNull BindingContext bindingContext
    ) {
        if (!canBeInlineArgument(argument)) return null;

        KtExpression call = KtPsiUtil.getParentCallIfPresent(argument);
        if (call == null) return null;

        ResolvedCall<?> resolvedCall = CallUtilKt.getResolvedCall(call, bindingContext);
        if (resolvedCall == null) return null;

        CallableDescriptor descriptor = resolvedCall.getResultingDescriptor();
        if (!isInline(descriptor) && !isArrayConstructorWithLambda(descriptor)) return null;

        ValueArgument valueArgument = CallUtilKt.getValueArgumentForExpression(resolvedCall.getCall(), argument);
        if (valueArgument == null) return null;

        ArgumentMapping mapping = resolvedCall.getArgumentMapping(valueArgument);
        if (!(mapping instanceof ArgumentMatch)) return null;

        ValueParameterDescriptor parameter = ((ArgumentMatch) mapping).getValueParameter();
        return isInlineParameter(parameter) ? parameter : null;
    }

    public static boolean canBeInlineArgument(@Nullable PsiElement functionalExpression) {
        return functionalExpression instanceof KtFunctionLiteral || functionalExpression instanceof KtNamedFunction;
    }

    /**
     * @return true if the descriptor is the constructor of one of 9 array classes (Array&lt;T&gt;, IntArray, FloatArray, ...)
     * which takes the size and an initializer lambda as parameters. Such constructors are marked as 'inline' but they are not loaded
     * as such because the 'inline' flag is not stored for constructors in the binary metadata. Therefore we pretend that they are inline
     */
    public static boolean isArrayConstructorWithLambda(@NotNull CallableDescriptor descriptor) {
        return descriptor.getValueParameters().size() == 2 &&
               descriptor instanceof ConstructorDescriptor &&
               KotlinBuiltIns.isArrayOrPrimitiveArray(((ConstructorDescriptor) descriptor).getConstructedClass());
    }

    @Nullable
    public static DeclarationDescriptor getContainingClassOrFunctionDescriptor(@NotNull DeclarationDescriptor descriptor, boolean strict) {
        DeclarationDescriptor current = strict ? descriptor.getContainingDeclaration() : descriptor;
        while (current != null) {
            if (current instanceof FunctionDescriptor || current instanceof ClassDescriptor) {
                return current;
            }
            current = current.getContainingDeclaration();
        }

        return null;
    }

    public static boolean allowsNonLocalReturns(@NotNull CallableDescriptor lambda) {
        if (lambda instanceof ValueParameterDescriptor) {
            if (((ValueParameterDescriptor) lambda).isCrossinline()) {
                //annotated
                return false;
            }
        }
        return true;
    }

    public static boolean containsReifiedTypeParameters(@NotNull CallableDescriptor descriptor) {
        for (TypeParameterDescriptor typeParameterDescriptor : descriptor.getTypeParameters()) {
            if (typeParameterDescriptor.isReified()) return true;
        }

        return false;
    }

    public static boolean isInlinableParameterExpression(@Nullable KtExpression deparenthesized) {
        return deparenthesized instanceof KtLambdaExpression ||
               deparenthesized instanceof KtNamedFunction ||
               deparenthesized instanceof KtCallableReferenceExpression;
    }
}
