/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.annotations.Annotated;

public interface DeclarationDescriptor extends Annotated, Named {
    /**
     * @return The descriptor that corresponds to the original declaration of this element.
     *         A descriptor can be obtained from its original by substituting type arguments (of the declaring class
     *         or of the element itself).
     *         returns <code>this</code> object if the current descriptor is original itself
     */
    @NotNull
    DeclarationDescriptor getOriginal();

    @Nullable
    DeclarationDescriptor getContainingDeclaration();

    <R, D> R accept(DeclarationDescriptorVisitor<R, D> visitor, D data);

    void acceptVoid(DeclarationDescriptorVisitor<Void, Void> visitor);
}
