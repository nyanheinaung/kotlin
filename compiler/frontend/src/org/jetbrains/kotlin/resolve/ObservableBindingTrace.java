/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve;

import com.intellij.util.SmartFMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.diagnostics.Diagnostic;
import org.jetbrains.kotlin.psi.KtExpression;
import org.jetbrains.kotlin.types.KotlinType;
import org.jetbrains.kotlin.util.slicedMap.ReadOnlySlice;
import org.jetbrains.kotlin.util.slicedMap.WritableSlice;

import java.util.Collection;

public class ObservableBindingTrace implements BindingTrace {
    public interface RecordHandler<K, V> {

        void handleRecord(WritableSlice<K, V> slice, K key, V value);
    }

    private final BindingTrace originalTrace;

    private SmartFMap<WritableSlice, RecordHandler> handlers = SmartFMap.emptyMap();

    public ObservableBindingTrace(BindingTrace originalTrace) {
        this.originalTrace = originalTrace;
    }
    @Override
    public void report(@NotNull Diagnostic diagnostic) {
        originalTrace.report(diagnostic);
    }

    @NotNull
    @Override
    public BindingContext getBindingContext() {
        return originalTrace.getBindingContext();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> void record(WritableSlice<K, V> slice, K key, V value) {
        originalTrace.record(slice, key, value);
        RecordHandler<K, V> recordHandler = (RecordHandler) handlers.get(slice);
        if (recordHandler != null) {
            recordHandler.handleRecord(slice, key, value);
        }
    }

    @Override
    public <K> void record(WritableSlice<K, Boolean> slice, K key) {
        record(slice, key, true);
    }

    @Override
    public <K, V> V get(ReadOnlySlice<K, V> slice, K key) {
        return originalTrace.get(slice, key);
    }

    @Override
    @NotNull
    public <K, V> Collection<K> getKeys(WritableSlice<K, V> slice) {
        return originalTrace.getKeys(slice);
    }

    @Nullable
    @Override
    public KotlinType getType(@NotNull KtExpression expression) {
        return originalTrace.getType(expression);
    }

    @Override
    public void recordType(@NotNull KtExpression expression, @Nullable KotlinType type) {
        originalTrace.recordType(expression, type);
    }

    public <K, V> ObservableBindingTrace addHandler(@NotNull WritableSlice<K, V> slice, @NotNull RecordHandler<K, V> handler) {
        handlers = handlers.plus(slice, handler);
        return this;
    }

    @Override
    public boolean wantsDiagnostics() {
        return originalTrace.wantsDiagnostics();
    }

    @Override
    public String toString() {
        return "ObservableTrace over " + originalTrace.toString();
    }
}
