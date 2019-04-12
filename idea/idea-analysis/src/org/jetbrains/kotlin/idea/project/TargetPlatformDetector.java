/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.project;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.platform.DefaultIdeTargetPlatformKindProvider;
import org.jetbrains.kotlin.platform.IdePlatform;
import org.jetbrains.kotlin.platform.IdePlatformKind;
import org.jetbrains.kotlin.psi.KtCodeFragment;
import org.jetbrains.kotlin.psi.KtFile;
import org.jetbrains.kotlin.psi.KtPsiFactoryKt;
import org.jetbrains.kotlin.resolve.TargetPlatform;
import org.jetbrains.kotlin.resolve.jvm.platform.JvmPlatform;
import org.jetbrains.kotlin.script.KotlinScriptDefinition;
import org.jetbrains.kotlin.scripting.shared.definitions.DefinitionsKt;

public class TargetPlatformDetector {
    public static final TargetPlatformDetector INSTANCE = new TargetPlatformDetector();
    private static final Logger LOG = Logger.getInstance(TargetPlatformDetector.class);

    private TargetPlatformDetector() {
    }

    @NotNull
    public static TargetPlatform getPlatform(@NotNull KtFile file) {
        TargetPlatform explicitPlatform = PlatformKt.getForcedTargetPlatform(file);
        if (explicitPlatform != null) return explicitPlatform;

        if (file instanceof KtCodeFragment) {
            KtFile contextFile = ((KtCodeFragment) file).getContextContainingFile();
            if (contextFile != null) {
                return getPlatform(contextFile);
            }
        }

        PsiElement context = KtPsiFactoryKt.getAnalysisContext(file);
        if (context != null) {
            PsiFile contextFile = context.getContainingFile();
            return contextFile instanceof KtFile ? getPlatform((KtFile) contextFile) : JvmPlatform.INSTANCE;
        }

        if (file.isScript()) {
            KotlinScriptDefinition scriptDefinition = DefinitionsKt.scriptDefinition(file);
            if (scriptDefinition != null) {
                String platformNameFromScriptDefinition = scriptDefinition.getPlatform();
                for (IdePlatform platform : IdePlatformKind.Companion.getAll_PLATFORMS()) {
                    TargetPlatform compilerPlatform = platform.getKind().getCompilerPlatform();
                    if (compilerPlatform.getPlatformName().equals(platformNameFromScriptDefinition)) {
                        return compilerPlatform;
                    }
                }
            }
        }

        VirtualFile virtualFile = file.getOriginalFile().getVirtualFile();
        if (virtualFile != null) {
            Module moduleForFile = ProjectFileIndex.SERVICE.getInstance(file.getProject()).getModuleForFile(virtualFile);
            if (moduleForFile != null) {
                return getPlatform(moduleForFile);
            }
        }

        return DefaultIdeTargetPlatformKindProvider.Companion.getDefaultCompilerPlatform();
    }

    @NotNull
    public static TargetPlatform getPlatform(@NotNull Module module) {
        return ProjectStructureUtil.getCachedPlatformForModule(module);
    }

}
