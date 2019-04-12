/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.translate.reference;

import org.jetbrains.kotlin.js.backend.ast.JsExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.descriptors.PropertyDescriptor;
import org.jetbrains.kotlin.descriptors.impl.SyntheticFieldDescriptorKt;
import org.jetbrains.kotlin.js.translate.context.TranslationContext;
import org.jetbrains.kotlin.js.translate.general.AbstractTranslator;
import org.jetbrains.kotlin.psi.KtSimpleNameExpression;

import static org.jetbrains.kotlin.js.translate.utils.BindingUtils.getDescriptorForReferenceExpression;
import static org.jetbrains.kotlin.js.translate.utils.TranslationUtils.assignmentToBackingField;
import static org.jetbrains.kotlin.js.translate.utils.TranslationUtils.backingFieldReference;

public final class BackingFieldAccessTranslator extends AbstractTranslator implements AccessTranslator {

    @NotNull
    private final PropertyDescriptor descriptor;

    /*package*/
    public static BackingFieldAccessTranslator newInstance(@NotNull KtSimpleNameExpression expression,
                                                    @NotNull TranslationContext context) {
        PropertyDescriptor referencedProperty = SyntheticFieldDescriptorKt.getReferencedProperty(
                getDescriptorForReferenceExpression(context.bindingContext(), expression)
        );
        assert referencedProperty != null;
        return new BackingFieldAccessTranslator(referencedProperty, context);
    }

    private BackingFieldAccessTranslator(@NotNull PropertyDescriptor descriptor, @NotNull TranslationContext context) {
        super(context);
        this.descriptor = descriptor;
    }

    @NotNull
    @Override
    public JsExpression translateAsGet() {
        return backingFieldReference(context(), descriptor);
    }

    @NotNull
    @Override
    public JsExpression translateAsSet(@NotNull JsExpression setTo) {
        return assignmentToBackingField(context(), descriptor, setTo);
    }

    @NotNull
    @Override
    public AccessTranslator getCached() {
        return this;
    }
}
