/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types;

public abstract class TypeProjectionBase implements TypeProjection {

    @Override
    public String toString() {
        if (isStarProjection()) {
            return "*";
        }
        if (getProjectionKind() == Variance.INVARIANT) {
            return getType().toString();
        }
        return getProjectionKind() + " " + getType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TypeProjection)) return false;

        TypeProjection that = (TypeProjection) o;

        if (isStarProjection() != that.isStarProjection()) return false;
        if (getProjectionKind() != that.getProjectionKind()) return false;
        if (!getType().equals(that.getType())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = getProjectionKind().hashCode();
        result = 31 * result + (isStarProjection() ? 17 : getType().hashCode());
        return result;
    }
}
