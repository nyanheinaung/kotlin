/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor;
import org.jetbrains.kotlin.psi.KtFile;
import org.jetbrains.org.objectweb.asm.Type;

public class MultifileClassPartContext extends MultifileClassContextBase implements DelegatingToPartContext, FacadePartWithSourceFile {
    private final KtFile sourceFile;

    public MultifileClassPartContext(
            PackageFragmentDescriptor descriptor,
            CodegenContext parent,
            Type multifileClassType,
            Type filePartType,
            @NotNull KtFile sourceFile
    ) {
        super(descriptor, parent, multifileClassType, filePartType);
        this.sourceFile = sourceFile;
    }

    @Nullable
    @Override
    public Type getImplementationOwnerClassType() {
        return getFilePartType();
    }

    @NotNull
    @Override
    public KtFile getSourceFile() {
        return sourceFile;
    }
}
