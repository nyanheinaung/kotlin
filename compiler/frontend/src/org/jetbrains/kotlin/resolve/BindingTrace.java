/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.diagnostics.DiagnosticSink;
import org.jetbrains.kotlin.psi.KtExpression;
import org.jetbrains.kotlin.types.KotlinType;
import org.jetbrains.kotlin.util.slicedMap.ReadOnlySlice;
import org.jetbrains.kotlin.util.slicedMap.WritableSlice;

import java.util.Collection;

public interface BindingTrace extends DiagnosticSink {

    @NotNull
    BindingContext getBindingContext();
    
    <K, V> void record(WritableSlice<K, V> slice, K key, V value);

    // Writes TRUE for a boolean value
    <K> void record(WritableSlice<K, Boolean> slice, K key);

    @Nullable
    <K, V> V get(ReadOnlySlice<K, V> slice, K key);

    // slice.isCollective() must be true
    @NotNull
    <K, V> Collection<K> getKeys(WritableSlice<K, V> slice);

    /**
     * Expression type should be taken from EXPRESSION_TYPE_INFO slice
     */
    @Nullable
    KotlinType getType(@NotNull KtExpression expression);

    /**
     * Expression type should be recorded into EXPRESSION_TYPE_INFO slice
     * (either updated old or a new one)
     */
    void recordType(@NotNull KtExpression expression, @Nullable KotlinType type);
}
