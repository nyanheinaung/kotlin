/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.optimization.boxing;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RedundantBoxedValuesCollection implements Iterable<BoxedValueDescriptor> {
    private final Set<BoxedValueDescriptor> safeToDeleteValues = new HashSet<>();

    public void add(@NotNull BoxedValueDescriptor descriptor) {
        safeToDeleteValues.add(descriptor);
    }

    public void remove(@NotNull BoxedValueDescriptor descriptor) {
        if (safeToDeleteValues.contains(descriptor)) {
            safeToDeleteValues.remove(descriptor);
            descriptor.markAsUnsafeToRemove();

            for (BoxedValueDescriptor mergedValueDescriptor : descriptor.getMergedWith()) {
                remove(mergedValueDescriptor);
            }
        }
    }

    public void merge(@NotNull BoxedValueDescriptor v, @NotNull BoxedValueDescriptor w) {
        v.addMergedWith(w);
        w.addMergedWith(v);

        if (v.isSafeToRemove() && !w.isSafeToRemove()) {
            remove(v);
        }

        if (!v.isSafeToRemove() && w.isSafeToRemove()) {
            remove(w);
        }
    }

    public boolean isEmpty() {
        return safeToDeleteValues.isEmpty();
    }

    @NotNull
    @Override
    public Iterator<BoxedValueDescriptor> iterator() {
        return safeToDeleteValues.iterator();
    }
}
