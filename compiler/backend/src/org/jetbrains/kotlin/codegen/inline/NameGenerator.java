/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class NameGenerator {

    private final String generatorClass;

    private int nextLambdaIndex = 1;
    private int nextWhenIndex = 1;

    private final Map<String, NameGenerator> subGenerators = new HashMap<>();

    public NameGenerator(String generatorClass) {
        this.generatorClass = generatorClass;
    }

    public String getGeneratorClass() {
        return generatorClass;
    }

    private String genLambdaClassName() {
        return generatorClass + "$" + nextLambdaIndex++;
    }

    private String genWhenClassName(@NotNull String original) {
        return generatorClass + "$" + nextWhenIndex++ + WhenMappingTransformationInfo.TRANSFORMED_WHEN_MAPPING_MARKER + original;
    }

    public NameGenerator subGenerator(String inliningMethod) {
        return subGenerators.computeIfAbsent(inliningMethod, method -> new NameGenerator(generatorClass + "$" + method));
    }

    @NotNull
    public NameGenerator subGenerator(boolean lambdaNoWhen, @Nullable String nameSuffix) {
        String generatorClass = lambdaNoWhen ? genLambdaClassName() : genWhenClassName(nameSuffix);
        assert !subGenerators.containsKey(generatorClass) : "Name generator for regenerated class should be unique: " + generatorClass;
        NameGenerator generator = new NameGenerator(generatorClass);
        subGenerators.put(generatorClass, generator);
        return generator;
    }
}
