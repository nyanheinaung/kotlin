/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorVisitor;
import org.jetbrains.kotlin.descriptors.annotations.AnnotatedImpl;
import org.jetbrains.kotlin.descriptors.annotations.Annotations;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.renderer.DescriptorRenderer;

public abstract class DeclarationDescriptorImpl extends AnnotatedImpl implements DeclarationDescriptor {

    @NotNull
    private final Name name;

    public DeclarationDescriptorImpl(@NotNull Annotations annotations, @NotNull Name name) {
        super(annotations);
        this.name = name;
    }

    @NotNull
    @Override
    public Name getName() {
        return name;
    }

    @NotNull
    @Override
    public DeclarationDescriptor getOriginal() {
        return this;
    }

    @Override
    public void acceptVoid(DeclarationDescriptorVisitor<Void, Void> visitor) {
        accept(visitor, null);
    }

    @Override
    public String toString() {
        return toString(this);
    }

    @NotNull
    public static String toString(@NotNull DeclarationDescriptor descriptor) {
        try {
            return DescriptorRenderer.DEBUG_TEXT.render(descriptor) +
                   "[" + descriptor.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(descriptor)) + "]";
        } catch (Throwable e) {
            // DescriptionRenderer may throw if this is not yet completely initialized
            // It is very inconvenient while debugging
            return descriptor.getClass().getSimpleName() + " " + descriptor.getName();
        }
    }
}
