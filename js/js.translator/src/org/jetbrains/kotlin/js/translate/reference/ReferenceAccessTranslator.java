/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.translate.reference;

import org.jetbrains.kotlin.js.backend.ast.JsExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.js.translate.context.TranslationContext;
import org.jetbrains.kotlin.js.translate.general.AbstractTranslator;
import org.jetbrains.kotlin.js.translate.utils.JsAstUtils;
import org.jetbrains.kotlin.psi.KtSimpleNameExpression;

import static org.jetbrains.kotlin.js.translate.reference.ReferenceTranslator.translateAsValueReference;
import static org.jetbrains.kotlin.js.translate.utils.BindingUtils.getDescriptorForReferenceExpression;

public final class ReferenceAccessTranslator extends AbstractTranslator implements AccessTranslator {

    @NotNull
    /*package*/ static ReferenceAccessTranslator newInstance(@NotNull KtSimpleNameExpression expression,
                                                             @NotNull TranslationContext context) {
        DeclarationDescriptor referenceDescriptor = getDescriptorForReferenceExpression(context.bindingContext(), expression);
        assert referenceDescriptor != null : "JetSimpleName expression must reference a descriptor " + expression.getText();
        return new ReferenceAccessTranslator(referenceDescriptor, context);
    }

    @NotNull
    private final JsExpression reference;

    private ReferenceAccessTranslator(@NotNull DeclarationDescriptor descriptor, @NotNull TranslationContext context) {
        super(context);
        this.reference = translateAsValueReference(descriptor, context());
    }

    @Override
    @NotNull
    public JsExpression translateAsGet() {
        return reference;
    }

    @Override
    @NotNull
    public JsExpression translateAsSet(@NotNull JsExpression toSetTo) {
        return JsAstUtils.assignment(reference, toSetTo);
    }

    @NotNull
    @Override
    public AccessTranslator getCached() {
        return this;
    }
}
