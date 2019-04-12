/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.codegen.OwnerKind;
import org.jetbrains.kotlin.codegen.state.GenerationState;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorVisitor;
import org.jetbrains.kotlin.descriptors.annotations.Annotations;
import org.jetbrains.kotlin.name.Name;

public class RootContext extends CodegenContext<RootContext.FakeDescriptor> {
    private final GenerationState state;

    public RootContext(@NotNull GenerationState state) {
        super(new FakeDescriptor(), OwnerKind.PACKAGE, null, null, null, null);
        this.state = state;
    }

    @Override
    @NotNull
    public GenerationState getState() {
        return state;
    }

    @Override
    public String toString() {
        return "ROOT";
    }

    static class FakeDescriptor implements DeclarationDescriptor {
        @NotNull
        @Override
        public DeclarationDescriptor getOriginal() {
            throw new IllegalStateException();
        }

        @Override
        public DeclarationDescriptor getContainingDeclaration() {
            throw new IllegalStateException();
        }

        @Override
        public <R, D> R accept(DeclarationDescriptorVisitor<R, D> visitor, D data) {
            throw new IllegalStateException();
        }

        @Override
        public void acceptVoid(DeclarationDescriptorVisitor<Void, Void> visitor) {
            throw new IllegalStateException();
        }

        @NotNull
        @Override
        public Annotations getAnnotations() {
            throw new IllegalStateException();
        }

        @NotNull
        @Override
        public Name getName() {
            throw new IllegalStateException();
        }
    }
}
