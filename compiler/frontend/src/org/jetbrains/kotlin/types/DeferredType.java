/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types;

import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.resolve.BindingTrace;
import org.jetbrains.kotlin.storage.NotNullLazyValue;
import org.jetbrains.kotlin.storage.StorageManager;
import org.jetbrains.kotlin.util.Box;
import org.jetbrains.kotlin.util.ReenteringLazyValueComputationException;

import static org.jetbrains.kotlin.resolve.BindingContext.DEFERRED_TYPE;

public class DeferredType extends WrappedType {
    private static final Function1<Boolean, KotlinType> RECURSION_PREVENTER = firstTime -> {
        if (firstTime) throw new ReenteringLazyValueComputationException();
        return ErrorUtils.createErrorType("Recursive dependency");
    };

    @NotNull
    /*package private*/ static DeferredType create(
            @NotNull StorageManager storageManager,
            @NotNull BindingTrace trace,
            @NotNull Function0<KotlinType> compute
    ) {
        DeferredType deferredType = new DeferredType(storageManager.createLazyValue(compute));
        trace.record(DEFERRED_TYPE, new Box<>(deferredType));
        return deferredType;
    }
    
    @NotNull
    /*package private*/ static DeferredType createRecursionIntolerant(
            @NotNull StorageManager storageManager,
            @NotNull BindingTrace trace,
            @NotNull Function0<KotlinType> compute
    ) {
        //noinspection unchecked
        DeferredType deferredType =
                new DeferredType(storageManager.createLazyValueWithPostCompute(compute, RECURSION_PREVENTER, t -> null));
        trace.record(DEFERRED_TYPE, new Box<>(deferredType));
        return deferredType;
    }

    private final NotNullLazyValue<KotlinType> lazyValue;

    private DeferredType(@NotNull NotNullLazyValue<KotlinType> lazyValue) {
        this.lazyValue = lazyValue;
    }

    public boolean isComputing() {
        return lazyValue.isComputing();
    }

    @Override
    public boolean isComputed() {
        return lazyValue.isComputed();
    }

    @NotNull
    @Override
    public KotlinType getDelegate() {
        return lazyValue.invoke();
    }

    @NotNull
    @Override
    public String toString() {
        try {
            if (lazyValue.isComputed()) {
                return getDelegate().toString();
            }
            else {
                return "<Not computed yet>";
            }
        }
        catch (ReenteringLazyValueComputationException e) {
            return "<Failed to compute this type>";
        }
    }
}
