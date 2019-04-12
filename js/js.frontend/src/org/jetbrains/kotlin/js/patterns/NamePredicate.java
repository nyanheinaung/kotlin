/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.patterns;

import com.google.common.collect.Lists;
import kotlin.collections.CollectionsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.builtins.PrimitiveType;
import org.jetbrains.kotlin.name.Name;

import java.util.*;
import java.util.function.Predicate;

public final class NamePredicate implements Predicate<Name> {

    @NotNull
    public static final NamePredicate PRIMITIVE_NUMBERS = new NamePredicate(
            CollectionsKt.map(PrimitiveType.NUMBER_TYPES, (PrimitiveType type) -> type.getTypeName().asString())
    );

    @NotNull
    public static final NamePredicate PRIMITIVE_NUMBERS_MAPPED_TO_PRIMITIVE_JS = new NamePredicate(
            CollectionsKt.mapNotNull(PrimitiveType.NUMBER_TYPES, (PrimitiveType type) ->
                    type != PrimitiveType.LONG ? type.getTypeName().asString() : null
            )
    );

    @NotNull
    public static final NamePredicate STRING = new NamePredicate("String");

    @NotNull
    public static final NamePredicate NUMBER = new NamePredicate("Number");

    @NotNull
    public static final NamePredicate BOOLEAN = new NamePredicate("Boolean");

    @NotNull
    public static final NamePredicate CHAR = new NamePredicate(PrimitiveType.CHAR.getTypeName());

    @NotNull
    public static final NamePredicate LONG = new NamePredicate(PrimitiveType.LONG.getTypeName());

    @NotNull
    private final Set<Name> validNames = new HashSet<>();

    public NamePredicate(@NotNull String... validNames) {
        this(Arrays.asList(validNames));
    }

    private NamePredicate(@NotNull List<String> validNames) {
        for (String validName : validNames) {
            this.validNames.add(Name.guessByFirstCharacter(validName));
        }
    }

    public NamePredicate(@NotNull Collection<Name> validNames) {
        this.validNames.addAll(validNames);
    }

    public NamePredicate(@NotNull Name... validNames) {
        this.validNames.addAll(Lists.newArrayList(validNames));
    }

    @Override
    public boolean test(Name name) {
        return validNames.contains(name);
    }
}
