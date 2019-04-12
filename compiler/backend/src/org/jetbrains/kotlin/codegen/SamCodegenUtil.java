/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.FunctionDescriptor;
import org.jetbrains.kotlin.descriptors.synthetic.SyntheticMemberDescriptor;
import org.jetbrains.kotlin.load.java.sam.SamAdapterDescriptor;
import org.jetbrains.kotlin.synthetic.SamAdapterExtensionFunctionDescriptor;

public class SamCodegenUtil {
    @Nullable
    @SuppressWarnings("unchecked")
    public static FunctionDescriptor getOriginalIfSamAdapter(@NotNull FunctionDescriptor fun) {
        if (fun instanceof SamAdapterDescriptor<?> || fun instanceof SamAdapterExtensionFunctionDescriptor) {
            return ((SyntheticMemberDescriptor<FunctionDescriptor>) fun).getBaseDescriptorForSynthetic();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends FunctionDescriptor> T resolveSamAdapter(@NotNull T descriptor) {
        FunctionDescriptor original = getOriginalIfSamAdapter(descriptor);
        return original != null ? (T) original : descriptor;
    }

    private SamCodegenUtil() {
    }
}
