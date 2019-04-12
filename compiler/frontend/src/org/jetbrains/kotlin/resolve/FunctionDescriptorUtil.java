/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve;

import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.descriptors.*;
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl;
import org.jetbrains.kotlin.resolve.scopes.*;
import org.jetbrains.kotlin.types.*;
import org.jetbrains.kotlin.types.typeUtil.TypeUtilsKt;

import java.util.List;

public class FunctionDescriptorUtil {
    private static final TypeSubstitutor MAKE_TYPE_PARAMETERS_FRESH = TypeSubstitutor.create(new TypeSubstitution() {
        @Override
        public TypeProjection get(@NotNull KotlinType key) {
            return null;
        }

        @Override
        public String toString() {
            return "FunctionDescriptorUtil.MAKE_TYPE_PARAMETERS_FRESH";
        }
    });

    private FunctionDescriptorUtil() {
    }

    public static TypeSubstitution createSubstitution(
            @NotNull FunctionDescriptor functionDescriptor,
            @NotNull List<KotlinType> typeArguments
    ) {
        if (functionDescriptor.getTypeParameters().isEmpty()) return TypeSubstitution.EMPTY;

        return new IndexedParametersSubstitution(functionDescriptor.getTypeParameters(), TypeUtilsKt.defaultProjections(typeArguments));
    }

    @NotNull
    public static LexicalScope getFunctionInnerScope(
            @NotNull LexicalScope outerScope, @NotNull FunctionDescriptor descriptor,
            @NotNull BindingTrace trace, @NotNull OverloadChecker overloadChecker
    ) {
        return getFunctionInnerScope(outerScope, descriptor, new TraceBasedLocalRedeclarationChecker(trace, overloadChecker));
    }

    @NotNull
    public static LexicalScope getFunctionInnerScope(
            @NotNull LexicalScope outerScope,
            @NotNull FunctionDescriptor descriptor,
            @NotNull LocalRedeclarationChecker redeclarationChecker
    ) {
        return new LexicalScopeImpl(
                outerScope, descriptor, true, descriptor.getExtensionReceiverParameter(),
                LexicalScopeKind.FUNCTION_INNER_SCOPE, redeclarationChecker,
                handler -> {
                    for (TypeParameterDescriptor typeParameter : descriptor.getTypeParameters()) {
                        handler.addClassifierDescriptor(typeParameter);
                    }
                    for (ValueParameterDescriptor valueParameterDescriptor : descriptor.getValueParameters()) {
                        if (valueParameterDescriptor instanceof ValueParameterDescriptorImpl.WithDestructuringDeclaration) {
                            List<VariableDescriptor> entries =
                                    ((ValueParameterDescriptorImpl.WithDestructuringDeclaration) valueParameterDescriptor)
                                            .getDestructuringVariables();
                            for (VariableDescriptor entry : entries) {
                                handler.addVariableDescriptor(entry);
                            }
                        }
                        else {
                            handler.addVariableDescriptor(valueParameterDescriptor);
                        }
                    }
                    return Unit.INSTANCE;
                }
        );
    }

    @SuppressWarnings("unchecked")
    public static <D extends CallableDescriptor> D alphaConvertTypeParameters(D candidate) {
        return (D) candidate.substitute(MAKE_TYPE_PARAMETERS_FRESH);
    }
}
