/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.when;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.codegen.binding.CodegenBinding;
import org.jetbrains.kotlin.codegen.state.GenerationState;
import org.jetbrains.kotlin.psi.KtWhenExpression;
import org.jetbrains.org.objectweb.asm.Type;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MappingsClassesForWhenByEnum {
    private final GenerationState state;
    private final Set<String> generatedMappingClasses = new HashSet<>();
    private final MappingClassesForWhenByEnumCodegen mappingsCodegen;

    public MappingsClassesForWhenByEnum(@NotNull GenerationState state) {
        this.state = state;
        this.mappingsCodegen = new MappingClassesForWhenByEnumCodegen(state);
    }

    public void generateMappingsClassForExpression(@NotNull KtWhenExpression expression) {
        WhenByEnumsMapping mapping = state.getBindingContext().get(CodegenBinding.MAPPING_FOR_WHEN_BY_ENUM, expression);

        assert mapping != null : "mapping class should not be requested for non enum when";

        if (!generatedMappingClasses.contains(mapping.getMappingsClassInternalName())) {
            List<WhenByEnumsMapping> mappings = state.getBindingContext().get(
                    CodegenBinding.MAPPINGS_FOR_WHENS_BY_ENUM_IN_CLASS_FILE,
                    mapping.getOuterClassInternalNameForExpression()
            );

            assert mappings != null : "guaranteed by usage contract of EnumSwitchCodegen";

            Type mappingsClassType = Type.getObjectType(mapping.getMappingsClassInternalName());

            mappingsCodegen.generate(mappings, mappingsClassType, expression.getContainingKtFile());
            generatedMappingClasses.add(mapping.getMappingsClassInternalName());
        }
    }
}
