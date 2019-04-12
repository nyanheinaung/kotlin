/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.codegen.ExpressionCodegen;
import org.jetbrains.kotlin.codegen.StackValue;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.types.KotlinType;
import org.jetbrains.org.objectweb.asm.Type;

public final class EnclosedValueDescriptor {
    private final String fieldName;
    private final DeclarationDescriptor descriptor;
    private final StackValue.StackValueWithSimpleReceiver innerValue;
    private final StackValue instanceValue;
    private final Type type;
    private final KotlinType kotlinType;

    public EnclosedValueDescriptor(
            @NotNull String fieldName,
            @Nullable DeclarationDescriptor descriptor,
            @NotNull StackValue.StackValueWithSimpleReceiver innerValue,
            @NotNull Type type,
            @Nullable KotlinType kotlinType
    ) {
        this.fieldName = fieldName;
        this.descriptor = descriptor;
        this.innerValue = innerValue;
        this.instanceValue = innerValue;
        this.type = type;
        this.kotlinType = kotlinType;
    }

    public EnclosedValueDescriptor(
            @NotNull String name,
            @Nullable DeclarationDescriptor descriptor,
            @NotNull StackValue.StackValueWithSimpleReceiver innerValue,
            @NotNull StackValue.Field instanceValue,
            @NotNull Type type,
            @Nullable KotlinType kotlinType
    ) {
        this.fieldName = name;
        this.descriptor = descriptor;
        this.innerValue = innerValue;
        this.instanceValue = instanceValue;
        this.type = type;
        this.kotlinType = kotlinType;
    }

    @NotNull
    public String getFieldName() {
        return fieldName;
    }

    @Nullable
    public DeclarationDescriptor getDescriptor() {
        return descriptor;
    }

    @NotNull
    public StackValue.StackValueWithSimpleReceiver getInnerValue() {
        return innerValue;
    }

    @NotNull
    public StackValue getInstanceValue() {
        return instanceValue;
    }

    @NotNull
    public Type getType() {
        return type;
    }

    @Nullable
    public KotlinType getKotlinType() {
        return kotlinType;
    }

    @Override
    public String toString() {
        return fieldName + " " + type + " -> " + descriptor;
    }
}
