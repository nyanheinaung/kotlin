/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.descriptors.ClassDescriptorWithResolutionScopes;
import org.jetbrains.kotlin.incremental.components.NoLookupLocation;
import org.jetbrains.kotlin.psi.*;
import org.jetbrains.kotlin.psi.psiUtil.PsiUtilsKt;
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo;
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfoFactory;
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassDescriptor;
import org.jetbrains.kotlin.resolve.scopes.LexicalScope;

public class DeclarationScopeProviderImpl implements DeclarationScopeProvider {

    private final LazyDeclarationResolver lazyDeclarationResolver;

    private final FileScopeProvider fileScopeProvider;

    public DeclarationScopeProviderImpl(
            @NotNull LazyDeclarationResolver lazyDeclarationResolver,
            @NotNull FileScopeProvider fileScopeProvider
    ) {
        this.lazyDeclarationResolver = lazyDeclarationResolver;
        this.fileScopeProvider = fileScopeProvider;
    }

    @Override
    @NotNull
    public LexicalScope getResolutionScopeForDeclaration(@NotNull PsiElement elementOfDeclaration) {
        KtDeclaration ktDeclaration = KtStubbedPsiUtil.getPsiOrStubParent(elementOfDeclaration, KtDeclaration.class, false);

        assert !(elementOfDeclaration instanceof KtDeclaration) || ktDeclaration == elementOfDeclaration :
                "For JetDeclaration element getParentOfType() should return itself.";
        assert ktDeclaration != null : "Should be contained inside declaration.";

        KtDeclaration parentDeclaration = KtStubbedPsiUtil.getContainingDeclaration(ktDeclaration);

        if (ktDeclaration instanceof KtPropertyAccessor) {
            parentDeclaration = KtStubbedPsiUtil.getContainingDeclaration(parentDeclaration, KtDeclaration.class);
        }

        if (parentDeclaration == null) {
            return fileScopeProvider.getFileResolutionScope((KtFile) elementOfDeclaration.getContainingFile());
        }

        if (parentDeclaration instanceof KtClassOrObject) {
            KtClassOrObject parentClassOrObject = (KtClassOrObject) parentDeclaration;
            LazyClassDescriptor parentClassDescriptor = (LazyClassDescriptor) lazyDeclarationResolver.getClassDescriptor(parentClassOrObject, NoLookupLocation.WHEN_GET_DECLARATION_SCOPE);

            if (ktDeclaration instanceof KtAnonymousInitializer || ktDeclaration instanceof KtProperty) {
                return parentClassDescriptor.getScopeForInitializerResolution();
            }

            if (ktDeclaration instanceof KtObjectDeclaration && ((KtObjectDeclaration) ktDeclaration).isCompanion()) {
                return parentClassDescriptor.getScopeForCompanionObjectHeaderResolution();
            }

            if (ktDeclaration instanceof KtObjectDeclaration ||
                ktDeclaration instanceof KtClass && !((KtClass) ktDeclaration).isInner()) {
                return parentClassDescriptor.getScopeForStaticMemberDeclarationResolution();
            }

            return parentClassDescriptor.getScopeForMemberDeclarationResolution();
        }
        //TODO: this is not how it works for classes and for exact parity we can try to use the code above
        if (parentDeclaration instanceof KtScript) {
            ClassDescriptorWithResolutionScopes
                    scriptDescriptor = (ClassDescriptorWithResolutionScopes) lazyDeclarationResolver.resolveToDescriptor(parentDeclaration);
            return scriptDescriptor.getScopeForInitializerResolution();
        }

        throw new IllegalStateException("Don't call this method for local declarations: " + ktDeclaration + "\n" +
                                        PsiUtilsKt.getElementTextWithContext(ktDeclaration));
    }

    @NotNull
    @Override
    public DataFlowInfo getOuterDataFlowInfoForDeclaration(@NotNull PsiElement elementOfDeclaration) {
        return DataFlowInfoFactory.EMPTY;
    }
}
