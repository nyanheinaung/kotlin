/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.filters;

import com.intellij.execution.filters.ExceptionFilterFactory;
import com.intellij.execution.filters.Filter;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

public class KotlinExceptionFilterFactory implements ExceptionFilterFactory {
    @NotNull
    @Override
    public Filter create(@NotNull GlobalSearchScope searchScope) {
        return new KotlinExceptionFilter(searchScope);
    }
}
