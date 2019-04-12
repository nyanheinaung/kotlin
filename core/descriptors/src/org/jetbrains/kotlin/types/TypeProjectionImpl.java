/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types;

import org.jetbrains.annotations.NotNull;

public class TypeProjectionImpl extends TypeProjectionBase {
    private final Variance projection;
    private final KotlinType type;

    public TypeProjectionImpl(@NotNull Variance projection, @NotNull KotlinType type) {
        this.projection = projection;
        this.type = type;
    }

    public TypeProjectionImpl(@NotNull KotlinType type) {
        this(Variance.INVARIANT, type);
    }

    @Override
    @NotNull
    public Variance getProjectionKind() {
        return projection;
    }

    @Override
    @NotNull
    public KotlinType getType() {
        return type;
    }

    @Override
    public boolean isStarProjection() {
        return false;
    }
}
