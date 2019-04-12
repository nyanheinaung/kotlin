/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.builtins.KotlinBuiltIns;
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubstitutionUtils {
    private SubstitutionUtils() {
    }

    /**
     * Builds a context with all the supertypes' parameters substituted
     */
    @NotNull
    public static TypeSubstitutor buildDeepSubstitutor(@NotNull KotlinType type) {
        Map<TypeConstructor, TypeProjection> substitution = new HashMap<>();
        TypeSubstitutor typeSubstitutor = TypeSubstitutor.create(substitution);
        // we use the mutability of the map here
        fillInDeepSubstitutor(type, typeSubstitutor, substitution, null);
        return typeSubstitutor;
    }

    /**
      For each supertype of a given type, we map type parameters to type arguments.

      For instance, we have the following class hierarchy:
          trait Iterable<out T>
          trait Collection<out E>: Iterable<E>
          trait MyFooCollection<F>: Collection<Foo<F>>

      For MyFooCollection<out CharSequence>, the following multimap will be returned:
          T declared in Iterable -> Foo<out CharSequence>
          E declared in Collection -> Foo<out CharSequence>
          F declared in MyFooCollection -> out CharSequence
     */
    @NotNull
    public static Multimap<TypeParameterDescriptor, TypeProjection> buildDeepSubstitutionMultimap(@NotNull KotlinType type) {
        Multimap<TypeParameterDescriptor, TypeProjection> fullSubstitution = LinkedHashMultimap.create();
        Map<TypeConstructor, TypeProjection> substitution = new HashMap<>();
        TypeSubstitutor typeSubstitutor = TypeSubstitutor.create(substitution);
        // we use the mutability of the map here
        fillInDeepSubstitutor(type, typeSubstitutor, substitution, fullSubstitution);
        return fullSubstitution;
    }

    // we use the mutability of the substitution map here
    private static void fillInDeepSubstitutor(
            @NotNull KotlinType context,
            @NotNull TypeSubstitutor substitutor,
            @NotNull Map<TypeConstructor, TypeProjection> substitution,
            @Nullable Multimap<TypeParameterDescriptor, TypeProjection> typeParameterMapping
    ) {
        List<TypeParameterDescriptor> parameters = context.getConstructor().getParameters();
        List<TypeProjection> arguments = context.getArguments();

        if (parameters.size() != arguments.size()) {
            throw new IllegalStateException();
        }

        for (int i = 0; i < arguments.size(); i++) {
            TypeProjection argument = arguments.get(i);
            TypeParameterDescriptor parameter = parameters.get(i);

            TypeProjection substitute = substitutor.substitute(argument);
            assert substitute != null;
            substitution.put(parameter.getTypeConstructor(), substitute);
            if (typeParameterMapping != null) {
                typeParameterMapping.put(parameter, substitute);
            }
        }
        if (KotlinBuiltIns.isNothingOrNullableNothing(context)) return;
        for (KotlinType supertype : context.getConstructor().getSupertypes()) {
            fillInDeepSubstitutor(supertype, substitutor, substitution, typeParameterMapping);
        }
    }
}
