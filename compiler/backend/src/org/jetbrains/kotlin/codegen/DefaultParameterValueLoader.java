/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen;

import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor;
import org.jetbrains.kotlin.psi.KtParameter;

import static org.jetbrains.kotlin.resolve.DescriptorToSourceUtils.descriptorToDeclaration;

public interface DefaultParameterValueLoader {
    StackValue genValue(ValueParameterDescriptor descriptor, ExpressionCodegen codegen);

    DefaultParameterValueLoader DEFAULT = (descriptor, codegen) -> {
        KtParameter ktParameter = (KtParameter) descriptorToDeclaration(descriptor);
        assert ktParameter != null;
        return codegen.gen(ktParameter.getDefaultValue());
    };
}
