/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.modules;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.modules.Module;

import java.util.Collections;
import java.util.List;

public class ModuleChunk {
    public static final ModuleChunk EMPTY = new ModuleChunk(Collections.emptyList());

    private final List<Module> modules;

    public ModuleChunk(@NotNull List<Module> modules) {
        this.modules = modules;
    }

    @NotNull
    public List<Module> getModules() {
        return modules;
    }
}
