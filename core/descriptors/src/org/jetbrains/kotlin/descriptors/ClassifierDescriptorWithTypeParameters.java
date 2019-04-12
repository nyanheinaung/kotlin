/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors;

import kotlin.annotations.jvm.ReadOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ClassifierDescriptorWithTypeParameters
        extends ClassifierDescriptor, DeclarationDescriptorWithVisibility, MemberDescriptor,
                Substitutable<ClassifierDescriptorWithTypeParameters> {
    /**
     * @return <code>true</code> if this class contains a reference to its outer class (as opposed to static nested class)
     */
    boolean isInner();

    @ReadOnly
    @NotNull
    List<TypeParameterDescriptor> getDeclaredTypeParameters();
}
