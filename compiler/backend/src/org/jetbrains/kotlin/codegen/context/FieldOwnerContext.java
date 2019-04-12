/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.codegen.AccessorForPropertyDescriptor;
import org.jetbrains.kotlin.codegen.OwnerKind;
import org.jetbrains.kotlin.codegen.binding.MutableClosure;
import org.jetbrains.kotlin.codegen.state.KotlinTypeMapper;
import org.jetbrains.kotlin.descriptors.ClassDescriptor;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.descriptors.PropertyDescriptor;
import org.jetbrains.kotlin.resolve.jvm.annotations.JvmAnnotationUtilKt;

import java.util.HashMap;
import java.util.Map;

public abstract class FieldOwnerContext<T extends DeclarationDescriptor> extends CodegenContext<T> {
    //default property name -> map<property descriptor -> bytecode name>
    private final Map<String, Map<PropertyDescriptor, String>> fieldNames = new HashMap<>();

    public FieldOwnerContext(
            @NotNull T contextDescriptor,
            @NotNull OwnerKind contextKind,
            @Nullable CodegenContext parentContext,
            @Nullable MutableClosure closure,
            @Nullable ClassDescriptor thisDescriptor,
            @Nullable LocalLookup expressionCodegen
    ) {
        super(contextDescriptor, contextKind, parentContext, closure, thisDescriptor, expressionCodegen);
    }

    @NotNull
    public String getFieldName(@NotNull PropertyDescriptor possiblySubstitutedDescriptor, boolean isDelegated) {
        if (possiblySubstitutedDescriptor instanceof AccessorForPropertyDescriptor) {
            possiblySubstitutedDescriptor = ((AccessorForPropertyDescriptor) possiblySubstitutedDescriptor).getCalleeDescriptor();
        }

        PropertyDescriptor descriptor = possiblySubstitutedDescriptor.getOriginal();
        assert descriptor.getKind().isReal() : "Only declared properties can have backing fields: " + descriptor;

        String defaultPropertyName = KotlinTypeMapper.mapDefaultFieldName(descriptor, isDelegated);

        Map<PropertyDescriptor, String> descriptor2Name = fieldNames.computeIfAbsent(defaultPropertyName, unused -> new HashMap<>());

        String actualName = descriptor2Name.get(descriptor);
        if (actualName != null) return actualName;

        String newName = descriptor2Name.isEmpty() || JvmAnnotationUtilKt.hasJvmFieldAnnotation(descriptor)
                         ? defaultPropertyName
                         : defaultPropertyName + "$" + descriptor2Name.size();
        descriptor2Name.put(descriptor, newName);
        return newName;
    }
}
