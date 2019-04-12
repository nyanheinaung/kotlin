/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.sam;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.ClassDescriptor;
import org.jetbrains.kotlin.descriptors.SourceElement;
import org.jetbrains.kotlin.descriptors.annotations.Annotations;
import org.jetbrains.kotlin.load.java.descriptors.JavaClassConstructorDescriptor;

/* package */ class SamAdapterClassConstructorDescriptor extends JavaClassConstructorDescriptor
        implements SamAdapterDescriptor<JavaClassConstructorDescriptor> {
    private final JavaClassConstructorDescriptor declaration;

    public SamAdapterClassConstructorDescriptor(@NotNull JavaClassConstructorDescriptor declaration) {
        super(declaration.getContainingDeclaration(), null, declaration.getAnnotations(),
              declaration.isPrimary(), Kind.SYNTHESIZED, declaration.getSource());
        this.declaration = declaration;
        setHasStableParameterNames(declaration.hasStableParameterNames());
        setHasSynthesizedParameterNames(declaration.hasSynthesizedParameterNames());
    }

    private SamAdapterClassConstructorDescriptor(
            @NotNull ClassDescriptor containingDeclaration,
            @Nullable JavaClassConstructorDescriptor original,
            @NotNull Annotations annotations,
            boolean isPrimary,
            @NotNull Kind kind,
            @NotNull SourceElement source,
            @NotNull JavaClassConstructorDescriptor declaration
    ) {
        super(containingDeclaration, original, annotations, isPrimary, kind, source);
        this.declaration = declaration;
    }

    @NotNull
    @Override
    protected JavaClassConstructorDescriptor createDescriptor(
            @NotNull ClassDescriptor newOwner,
            @Nullable JavaClassConstructorDescriptor original,
            @NotNull Kind kind,
            @NotNull SourceElement sourceElement,
            @NotNull Annotations annotations
    ) {
        return new SamAdapterClassConstructorDescriptor(newOwner, original, annotations, isPrimary, kind, sourceElement, declaration);
    }

    @NotNull
    @Override
    public JavaClassConstructorDescriptor getBaseDescriptorForSynthetic() {
        return declaration;
    }
}
