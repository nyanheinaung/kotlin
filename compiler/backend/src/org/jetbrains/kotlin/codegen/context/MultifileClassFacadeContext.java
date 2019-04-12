/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.context;

import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor;
import org.jetbrains.org.objectweb.asm.Type;

public class MultifileClassFacadeContext extends MultifileClassContextBase {
    public MultifileClassFacadeContext(
            PackageFragmentDescriptor descriptor,
            CodegenContext parent,
            Type multifileClassType,
            Type filePartType
    ) {
        super(descriptor, parent, multifileClassType, filePartType);
    }
}
