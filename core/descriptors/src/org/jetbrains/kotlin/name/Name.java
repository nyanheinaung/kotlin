/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.name;

import org.jetbrains.annotations.NotNull;

public final class Name implements Comparable<Name> {
    @NotNull
    private final String name;
    private final boolean special;

    private Name(@NotNull String name, boolean special) {
        this.name = name;
        this.special = special;
    }

    @NotNull
    public String asString() {
        return name;
    }

    @NotNull
    public String getIdentifier() {
        if (special) {
            throw new IllegalStateException("not identifier: " + this);
        }
        return asString();
    }

    public boolean isSpecial() {
        return special;
    }

    @Override
    public int compareTo(Name that) {
        return this.name.compareTo(that.name);
    }

    @NotNull
    public static Name identifier(@NotNull String name) {
        return new Name(name, false);
    }

    public static boolean isValidIdentifier(@NotNull String name) {
        if (name.isEmpty() || name.startsWith("<")) return false;
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if (ch == '.' || ch == '/' || ch == '\\') {
                return false;
            }
        }

        return true;
    }

    @NotNull
    public static Name special(@NotNull String name) {
        if (!name.startsWith("<")) {
            throw new IllegalArgumentException("special name must start with '<': " + name);
        }
        return new Name(name, true);
    }

    @NotNull
    public static Name guessByFirstCharacter(@NotNull String name) {
        if (name.startsWith("<")) {
            return special(name);
        }
        else {
            return identifier(name);
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Name)) return false;

        Name name1 = (Name) o;

        if (special != name1.special) return false;
        if (!name.equals(name1.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (special ? 1 : 0);
        return result;
    }
}
