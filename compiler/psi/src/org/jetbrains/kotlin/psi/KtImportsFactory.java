/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.openapi.project.Project;
import kotlin.collections.CollectionsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.resolve.ImportPath;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @deprecated This class is not used in the kotlin plugin/compiler and will be removed soon
 */
@Deprecated
public class KtImportsFactory {
    @NotNull private final Project project;

    private final Map<ImportPath, KtImportDirective> importsCache = new HashMap<>();

    public KtImportsFactory(@NotNull Project project) {
        this.project = project;
    }

    @NotNull
    private KtImportDirective createImportDirective(@NotNull ImportPath importPath) {
        KtImportDirective directive = importsCache.get(importPath);
        if (directive != null) {
            return directive;
        }

        KtImportDirective createdDirective = KtPsiFactoryKt.KtPsiFactory(project, false).createImportDirective(importPath);
        importsCache.put(importPath, createdDirective);

        return createdDirective;
    }

    @NotNull
    public Collection<KtImportDirective> createImportDirectives(@NotNull Collection<ImportPath> importPaths) {
        return CollectionsKt.map(importPaths, this::createImportDirective);
    }

    @NotNull
    public Collection<KtImportDirective> createImportDirectivesNotCached(@NotNull Collection<ImportPath> importPaths) {
        return KtPsiFactoryKt.KtPsiFactory(project, false).createImportDirectives(importPaths);
    }
}
