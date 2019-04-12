/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.descriptors.SourceElement;
import org.jetbrains.kotlin.descriptors.SupertypeLoopChecker;
import org.jetbrains.kotlin.descriptors.annotations.Annotations;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.storage.StorageManager;
import org.jetbrains.kotlin.types.Variance;

public abstract class AbstractLazyTypeParameterDescriptor extends AbstractTypeParameterDescriptor {
    public AbstractLazyTypeParameterDescriptor(
            @NotNull StorageManager storageManager,
            @NotNull DeclarationDescriptor containingDeclaration,
            @NotNull Name name,
            @NotNull Variance variance,
            boolean isReified,
            int index,
            @NotNull SourceElement source,
            @NotNull SupertypeLoopChecker supertypeLoopChecker
    ) {
        super(storageManager, containingDeclaration, Annotations.Companion.getEMPTY() /* TODO */, name, variance, isReified, index, source,
              supertypeLoopChecker);
    }

    @Override
    public String toString() {
        // Not using descriptor renderer to preserve laziness
        return String.format(
                "%s%s%s",
                isReified() ? "reified " : "",
                getVariance() == Variance.INVARIANT ? "" : getVariance() + " ",
                getName()
        );
    }
}
