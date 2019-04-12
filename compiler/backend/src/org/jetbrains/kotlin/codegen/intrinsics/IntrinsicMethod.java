/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.intrinsics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.codegen.Callable;
import org.jetbrains.kotlin.codegen.CallableMethod;
import org.jetbrains.kotlin.codegen.ExpressionCodegen;
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor;
import org.jetbrains.kotlin.descriptors.FunctionDescriptor;
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall;
import org.jetbrains.kotlin.resolve.jvm.AsmTypes;
import org.jetbrains.org.objectweb.asm.Type;

public abstract class IntrinsicMethod {
    @NotNull
    public Callable toCallable(
            @NotNull FunctionDescriptor fd,
            boolean isSuper,
            @NotNull ResolvedCall resolvedCall,
            @NotNull ExpressionCodegen codegen
    ) {
        return toCallable(codegen.getState().getTypeMapper().mapToCallableMethod(fd, false), isSuper, resolvedCall);
    }

    public boolean isApplicableToOverload(@NotNull CallableMemberDescriptor descriptor) {
        return true;
    }

    @NotNull
    protected Callable toCallable(@NotNull CallableMethod method, boolean isSuper, @NotNull ResolvedCall resolvedCall) {
        return toCallable(method, isSuper);
    }

    @NotNull
    protected Callable toCallable(@NotNull CallableMethod method, boolean isSuperCall) {
        return toCallable(method);
    }

    @NotNull
    protected Callable toCallable(@NotNull CallableMethod method) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public Type nullOrObject(Type type) {
        return nullOr(type, AsmTypes.OBJECT_TYPE);
    }

    public Type nullOr(Type type, Type newType) {
        return type == null ? null : newType;
    }
}
