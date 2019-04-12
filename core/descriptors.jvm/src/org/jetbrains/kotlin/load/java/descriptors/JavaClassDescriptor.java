/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.descriptors;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.ClassDescriptor;
import org.jetbrains.kotlin.types.SimpleType;

public interface JavaClassDescriptor extends ClassDescriptor {
    // Use SingleAbstractMethodUtils.getFunctionTypeForSamInterface() where possible. This is only a fallback
    @Nullable
    SimpleType getDefaultFunctionTypeForSamInterface();

    /**
     * May return false even in case when the class is not SAM interface, but returns true only if it's definitely not a SAM.
     * But it should work much faster than the exact check.
     */
    boolean isDefinitelyNotSamInterface();
}
