/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.name;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.utils.StringsKt;

import java.util.List;

public final class FqName {

    @NotNull
    public static FqName fromSegments(@NotNull List<String> names) {
        return new FqName(StringsKt.join(names, "."));
    }

    public static final FqName ROOT = new FqName("");

    @NotNull
    private final FqNameUnsafe fqName;

    // cache
    private transient FqName parent;

    public FqName(@NotNull String fqName) {
        this.fqName = new FqNameUnsafe(fqName, this);
    }

    public FqName(@NotNull FqNameUnsafe fqName) {
        this.fqName = fqName;
    }

    private FqName(@NotNull FqNameUnsafe fqName, FqName parent) {
        this.fqName = fqName;
        this.parent = parent;
    }

    @NotNull
    public String asString() {
        return fqName.asString();
    }

    @NotNull
    public FqNameUnsafe toUnsafe() {
        return fqName;
    }

    public boolean isRoot() {
        return fqName.isRoot();
    }

    @NotNull
    public FqName parent() {
        if (parent != null) {
            return parent;
        }

        if (isRoot()) {
            throw new IllegalStateException("root");
        }

        parent = new FqName(fqName.parent());

        return parent;
    }

    @NotNull
    public FqName child(@NotNull Name name) {
        return new FqName(fqName.child(name), this);
    }

    @NotNull
    public Name shortName() {
        return fqName.shortName();
    }

    @NotNull
    public Name shortNameOrSpecial() {
        return fqName.shortNameOrSpecial();
    }

    @NotNull
    public List<Name> pathSegments() {
        return fqName.pathSegments();
    }

    public boolean startsWith(@NotNull Name segment) {
        return fqName.startsWith(segment);
    }

    @NotNull
    public static FqName topLevel(@NotNull Name shortName) {
        return new FqName(FqNameUnsafe.topLevel(shortName));
    }

    @Override
    public String toString() {
        return fqName.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FqName)) return false;

        FqName otherFqName = (FqName) o;

        if (!fqName.equals(otherFqName.fqName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return fqName.hashCode();
    }
}
