/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.context;

import org.jetbrains.kotlin.codegen.OwnerKind;
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor;
import org.jetbrains.org.objectweb.asm.Type;

public class MultifileClassContextBase extends FieldOwnerContext<PackageFragmentDescriptor> {
    private final Type multifileClassType;
    private final Type filePartType;

    public MultifileClassContextBase(
            PackageFragmentDescriptor descriptor,
            CodegenContext parent,
            Type multifileClassType,
            Type filePartType
    ) {
        super(descriptor, OwnerKind.PACKAGE, parent, null, null, null);
        this.multifileClassType = multifileClassType;
        this.filePartType = filePartType;
    }

    public Type getMultifileClassType() {
        return multifileClassType;
    }

    public Type getFilePartType() {
        return filePartType;
    }
}
