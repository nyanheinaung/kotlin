/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor;

public interface PackageCodegen {
    void generate(@NotNull CompilationErrorHandler errorHandler);

    PackageFragmentDescriptor getPackageFragment();
}
