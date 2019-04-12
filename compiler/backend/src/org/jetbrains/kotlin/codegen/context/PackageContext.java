/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.codegen.OwnerKind;
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor;
import org.jetbrains.kotlin.psi.KtFile;
import org.jetbrains.org.objectweb.asm.Type;

public class PackageContext extends FieldOwnerContext<PackageFragmentDescriptor> implements DelegatingToPartContext, FacadePartWithSourceFile {
    private final Type packagePartType;
    private final KtFile sourceFile;

    public PackageContext(
            @NotNull PackageFragmentDescriptor contextDescriptor,
            @NotNull CodegenContext parent,
            @Nullable Type packagePartType,
            @Nullable KtFile sourceFile
    ) {
        super(contextDescriptor, OwnerKind.PACKAGE, parent, null, null, null);
        this.packagePartType = packagePartType;
        this.sourceFile = sourceFile;
    }

    @Override
    public String toString() {
        return "Package: " + getContextDescriptor().getName();
    }

    @Nullable
    @Override
    public Type getImplementationOwnerClassType() {
        return packagePartType;
    }

    @Nullable
    @Override
    public KtFile getSourceFile() {
        return sourceFile;
    }
}
