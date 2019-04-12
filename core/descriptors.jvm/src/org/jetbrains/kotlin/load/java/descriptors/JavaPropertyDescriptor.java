/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.descriptors;

import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.builtins.KotlinBuiltIns;
import org.jetbrains.kotlin.descriptors.*;
import org.jetbrains.kotlin.descriptors.annotations.Annotations;
import org.jetbrains.kotlin.descriptors.impl.PropertyDescriptorImpl;
import org.jetbrains.kotlin.descriptors.impl.PropertyGetterDescriptorImpl;
import org.jetbrains.kotlin.descriptors.impl.PropertySetterDescriptorImpl;
import org.jetbrains.kotlin.load.java.typeEnhancement.TypeEnhancementKt;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.resolve.DescriptorFactory;
import org.jetbrains.kotlin.types.KotlinType;

import java.util.List;

public class JavaPropertyDescriptor extends PropertyDescriptorImpl implements JavaCallableMemberDescriptor {
    private final boolean isStaticFinal;
    @Nullable
    private final Pair<UserDataKey<?>, ?> singleUserData;

    private JavaPropertyDescriptor(
            @NotNull DeclarationDescriptor containingDeclaration,
            @NotNull Annotations annotations,
            @NotNull Modality modality,
            @NotNull Visibility visibility,
            boolean isVar,
            @NotNull Name name,
            @NotNull SourceElement source,
            @Nullable PropertyDescriptor original,
            @NotNull Kind kind,
            boolean isStaticFinal,
            @Nullable Pair<UserDataKey<?>, ?> singleUserData
    ) {
        super(containingDeclaration, original, annotations, modality, visibility, isVar, name, kind, source,
              false, false, false, false, false, false);

        this.isStaticFinal = isStaticFinal;
        this.singleUserData = singleUserData;
    }

    @NotNull
    public static JavaPropertyDescriptor create(
            @NotNull DeclarationDescriptor containingDeclaration,
            @NotNull Annotations annotations,
            @NotNull Modality modality,
            @NotNull Visibility visibility,
            boolean isVar,
            @NotNull Name name,
            @NotNull SourceElement source,
            boolean isStaticFinal
    ) {
        return new JavaPropertyDescriptor(
                containingDeclaration, annotations, modality, visibility, isVar, name, source, null, Kind.DECLARATION, isStaticFinal,
                null);
    }

    @NotNull
    @Override
    protected PropertyDescriptorImpl createSubstitutedCopy(
            @NotNull DeclarationDescriptor newOwner,
            @NotNull Modality newModality,
            @NotNull Visibility newVisibility,
            @Nullable PropertyDescriptor original,
            @NotNull Kind kind,
            @NotNull Name newName
    ) {
        return new JavaPropertyDescriptor(
                newOwner, getAnnotations(), newModality, newVisibility, isVar(), newName, SourceElement.NO_SOURCE, original,
                kind, isStaticFinal,
                singleUserData);
    }

    @Override
    public boolean hasSynthesizedParameterNames() {
        return false;
    }

    @NotNull
    @Override
    public JavaCallableMemberDescriptor enhance(
            @Nullable KotlinType enhancedReceiverType,
            @NotNull List<ValueParameterData> enhancedValueParametersData,
            @NotNull KotlinType enhancedReturnType,
            @Nullable Pair<UserDataKey<?>, ?> additionalUserData
    ) {
        PropertyDescriptor enhancedOriginal = getOriginal() == this ? null : getOriginal();
        JavaPropertyDescriptor enhanced = new JavaPropertyDescriptor(
                getContainingDeclaration(),
                getAnnotations(),
                getModality(),
                getVisibility(),
                isVar(),
                getName(),
                getSource(),
                enhancedOriginal,
                getKind(),
                isStaticFinal,
                additionalUserData);

        PropertyGetterDescriptorImpl newGetter = null;
        PropertyGetterDescriptorImpl getter = getGetter();
        if (getter != null) {
            newGetter = new PropertyGetterDescriptorImpl(
                    enhanced, getter.getAnnotations(), getter.getModality(), getter.getVisibility(),
                    getter.isDefault(), getter.isExternal(), getter.isInline(), getKind(),
                    enhancedOriginal == null ? null : enhancedOriginal.getGetter(),
                    getter.getSource()
            );
            newGetter.setInitialSignatureDescriptor(getter.getInitialSignatureDescriptor());
            newGetter.initialize(enhancedReturnType);
        }

        PropertySetterDescriptorImpl newSetter = null;
        PropertySetterDescriptor setter = getSetter();
        if (setter != null) {
            newSetter = new PropertySetterDescriptorImpl(
                    enhanced, setter.getAnnotations(), setter.getModality(), setter.getVisibility(),
                    setter.isDefault(), setter.isExternal(), setter.isInline(), getKind(),
                    enhancedOriginal == null ? null : enhancedOriginal.getSetter(),
                    setter.getSource()
            );
            newSetter.setInitialSignatureDescriptor(newSetter.getInitialSignatureDescriptor());
            newSetter.initialize(setter.getValueParameters().get(0));
        }

        enhanced.initialize(newGetter, newSetter, getBackingField(), getDelegateField());
        enhanced.setSetterProjectedOut(isSetterProjectedOut());
        if (compileTimeInitializer != null) {
            enhanced.setCompileTimeInitializer(compileTimeInitializer);
        }

        enhanced.setOverriddenDescriptors(getOverriddenDescriptors());

        ReceiverParameterDescriptor enhancedReceiver =
                enhancedReceiverType == null ? null : DescriptorFactory.createExtensionReceiverParameterForCallable(
                        this, enhancedReceiverType, Annotations.Companion.getEMPTY()
                );

        enhanced.setType(
                enhancedReturnType,
                getTypeParameters(), // TODO
                getDispatchReceiverParameter(),
                enhancedReceiver
        );
        return enhanced;
    }

    @Override
    public boolean isConst() {
        KotlinType type = getType();
        return isStaticFinal && ConstUtil.canBeUsedForConstVal(type) &&
               (!TypeEnhancementKt.hasEnhancedNullability(type) || KotlinBuiltIns.isString(type));
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <V> V getUserData(UserDataKey<V> key) {
        if (singleUserData != null && singleUserData.getFirst().equals(key)) {
            return (V) singleUserData.getSecond();
        }

        return null;
    }
}
