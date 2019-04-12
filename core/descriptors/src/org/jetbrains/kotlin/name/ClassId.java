/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.name;

import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A class name which is used to uniquely identify a Kotlin class.
 *
 * If local = true, the class represented by this id is either itself local or is an inner class of some local class. This also means that
 * the first non-class container of the class is not a package.
 * In the case of a local class, relativeClassName consists of a single name including all callables' and class' names all the way up to
 * the package, separated by dollar signs. If a class is an inner of local, relativeClassName would consist of two names,
 * the second one being the class' short name.
 */
public final class ClassId {
    @NotNull
    public static ClassId topLevel(@NotNull FqName topLevelFqName) {
        return new ClassId(topLevelFqName.parent(), topLevelFqName.shortName());
    }

    private final FqName packageFqName;
    private final FqName relativeClassName;
    private final boolean local;

    public ClassId(@NotNull FqName packageFqName, @NotNull FqName relativeClassName, boolean local) {
        this.packageFqName = packageFqName;
        assert !relativeClassName.isRoot() :
                "Class name must not be root: " + packageFqName + (local ? " (local)" : "");
        this.relativeClassName = relativeClassName;
        this.local = local;
    }

    public ClassId(@NotNull FqName packageFqName, @NotNull Name topLevelName) {
        this(packageFqName, FqName.topLevel(topLevelName), false);
    }

    @NotNull
    public FqName getPackageFqName() {
        return packageFqName;
    }

    @NotNull
    public FqName getRelativeClassName() {
        return relativeClassName;
    }

    @NotNull
    public Name getShortClassName() {
        return relativeClassName.shortName();
    }

    public boolean isLocal() {
        return local;
    }

    @NotNull
    public ClassId createNestedClassId(@NotNull Name name) {
        return new ClassId(getPackageFqName(), relativeClassName.child(name), local);
    }

    @Nullable
    public ClassId getOuterClassId() {
        FqName parent = relativeClassName.parent();
        return parent.isRoot() ? null : new ClassId(getPackageFqName(), parent, local);
    }

    public boolean isNestedClass() {
        return !relativeClassName.parent().isRoot();
    }

    @NotNull
    public FqName asSingleFqName() {
        if (packageFqName.isRoot()) return relativeClassName;
        return new FqName(packageFqName.asString() + "." + relativeClassName.asString());
    }

    public boolean startsWith(@NotNull Name segment) {
        return packageFqName.startsWith(segment);
    }

    /**
     * @param string a string where packages are delimited by '/' and classes by '.', e.g. "kotlin/Map.Entry"
     */
    @NotNull
    public static ClassId fromString(@NotNull String string) {
        return fromString(string, false);
    }

    @NotNull
    public static ClassId fromString(@NotNull String string, boolean isLocal) {
        String packageName = StringsKt.substringBeforeLast(string, '/', "").replace('/', '.');
        String className = StringsKt.substringAfterLast(string, '/', string);
        return new ClassId(new FqName(packageName), new FqName(className), isLocal);
    }

    /**
     * @return a string where packages are delimited by '/' and classes by '.', e.g. "kotlin/Map.Entry"
     */
    @NotNull
    public String asString() {
        if (packageFqName.isRoot()) return relativeClassName.asString();
        return packageFqName.asString().replace('.', '/') + "/" + relativeClassName.asString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassId id = (ClassId) o;

        return packageFqName.equals(id.packageFqName) &&
               relativeClassName.equals(id.relativeClassName) &&
               local == id.local;
    }

    @Override
    public int hashCode() {
        int result = packageFqName.hashCode();
        result = 31 * result + relativeClassName.hashCode();
        result = 31 * result + Boolean.valueOf(local).hashCode();
        return result;
    }

    @Override
    public String toString() {
        return packageFqName.isRoot() ? "/" + asString() : asString();
    }
}
