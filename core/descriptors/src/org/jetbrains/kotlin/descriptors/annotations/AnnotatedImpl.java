/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.annotations;

import org.jetbrains.annotations.NotNull;

public class AnnotatedImpl implements Annotated {
    private final Annotations annotations;

    public AnnotatedImpl(@NotNull Annotations annotations) {
        this.annotations = annotations;
    }

    @NotNull
    @Override
    public Annotations getAnnotations() {
        return annotations;
    }
}
