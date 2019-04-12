/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.*;
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor;
import org.jetbrains.kotlin.descriptors.annotations.Annotations;
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl;
import org.jetbrains.kotlin.resolve.DescriptorUtils;
import org.jetbrains.kotlin.resolve.scopes.MemberScope;
import org.jetbrains.kotlin.types.FlexibleTypesKt;
import org.jetbrains.kotlin.types.KotlinType;
import org.jetbrains.kotlin.types.TypeConstructor;
import org.jetbrains.kotlin.types.TypeProjection;

import java.util.Collection;

public class ForceResolveUtil {
    private static final Logger LOG = Logger.getInstance(ForceResolveUtil.class);

    private ForceResolveUtil() {}

    public static <T> T forceResolveAllContents(@NotNull T descriptor) {
        doForceResolveAllContents(descriptor);
        return descriptor;
    }

    public static void forceResolveAllContents(@NotNull MemberScope scope) {
        forceResolveAllContents(DescriptorUtils.getAllDescriptors(scope));
    }

    public static void forceResolveAllContents(@NotNull Iterable<? extends DeclarationDescriptor> descriptors) {
        for (DeclarationDescriptor descriptor : descriptors) {
            forceResolveAllContents(descriptor);
        }
    }

    public static void forceResolveAllContents(@NotNull Collection<KotlinType> types) {
        for (KotlinType type : types) {
            forceResolveAllContents(type);
        }
    }

    public static void forceResolveAllContents(@NotNull TypeConstructor typeConstructor) {
        doForceResolveAllContents(typeConstructor);
    }

    public static void forceResolveAllContents(@NotNull Annotations annotations) {
        doForceResolveAllContents(annotations);
        for (AnnotationDescriptor annotation : annotations) {
            doForceResolveAllContents(annotation);
        }
    }

    private static void doForceResolveAllContents(Object object) {
        if (object instanceof LazyEntity) {
            LazyEntity lazyEntity = (LazyEntity) object;
            lazyEntity.forceResolveAllContents();
        }
        else if (object instanceof ValueParameterDescriptorImpl.WithDestructuringDeclaration) {
            ((ValueParameterDescriptorImpl.WithDestructuringDeclaration) object).getDestructuringVariables();
        }
        else if (object instanceof CallableDescriptor) {
            CallableDescriptor callableDescriptor = (CallableDescriptor) object;
            ReceiverParameterDescriptor parameter = callableDescriptor.getExtensionReceiverParameter();
            if (parameter != null) {
                forceResolveAllContents(parameter.getType());
            }
            for (ValueParameterDescriptor parameterDescriptor : callableDescriptor.getValueParameters()) {
                forceResolveAllContents(parameterDescriptor);
            }
            for (TypeParameterDescriptor typeParameterDescriptor : callableDescriptor.getTypeParameters()) {
                forceResolveAllContents(typeParameterDescriptor.getUpperBounds());
            }
            forceResolveAllContents(callableDescriptor.getReturnType());
            forceResolveAllContents(callableDescriptor.getAnnotations());
        }
        else if (object instanceof TypeAliasDescriptor) {
            TypeAliasDescriptor typeAliasDescriptor = (TypeAliasDescriptor) object;
            forceResolveAllContents(typeAliasDescriptor.getUnderlyingType());
        }
    }

    @Nullable
    public static KotlinType forceResolveAllContents(@Nullable KotlinType type) {
        if (type == null) return null;

        forceResolveAllContents(type.getAnnotations());
        if (FlexibleTypesKt.isFlexible(type)) {
            forceResolveAllContents(FlexibleTypesKt.asFlexibleType(type).getLowerBound());
            forceResolveAllContents(FlexibleTypesKt.asFlexibleType(type).getUpperBound());
        }
        else {
            forceResolveAllContents(type.getConstructor());
            for (TypeProjection projection : type.getArguments()) {
                if (!projection.isStarProjection()) {
                    forceResolveAllContents(projection.getType());
                }
            }
        }
        return type;
    }
}
