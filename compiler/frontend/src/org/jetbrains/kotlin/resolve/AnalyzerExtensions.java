/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.config.LanguageVersionSettings;
import org.jetbrains.kotlin.descriptors.*;
import org.jetbrains.kotlin.psi.KtCallableDeclaration;
import org.jetbrains.kotlin.psi.KtNamedFunction;
import org.jetbrains.kotlin.psi.KtProperty;
import org.jetbrains.kotlin.resolve.inline.InlineAnalyzerExtension;
import org.jetbrains.kotlin.resolve.inline.InlineUtil;
import org.jetbrains.kotlin.resolve.inline.ReasonableInlineRule;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AnalyzerExtensions {

    public interface AnalyzerExtension {
        void process(@NotNull CallableMemberDescriptor descriptor, @NotNull KtCallableDeclaration functionOrProperty, @NotNull BindingTrace trace);
    }

    @NotNull private final BindingTrace trace;
    @NotNull private final Iterable<ReasonableInlineRule> reasonableInlineRules;
    private LanguageVersionSettings languageVersionSettings;

    public AnalyzerExtensions(
            @NotNull BindingTrace trace,
            @NotNull Iterable<ReasonableInlineRule> reasonableInlineRules,
            @NotNull LanguageVersionSettings languageVersionSettings
    ) {
        this.trace = trace;
        this.reasonableInlineRules = reasonableInlineRules;
        this.languageVersionSettings = languageVersionSettings;
    }

    public void process(@NotNull BodiesResolveContext bodiesResolveContext) {
        for (Map.Entry<KtNamedFunction, SimpleFunctionDescriptor> entry : bodiesResolveContext.getFunctions().entrySet()) {
            KtNamedFunction function = entry.getKey();
            SimpleFunctionDescriptor functionDescriptor = entry.getValue();

            for (AnalyzerExtension extension : getFunctionExtensions(functionDescriptor)) {
                extension.process(functionDescriptor, function, trace);
            }
        }

        for (Map.Entry<KtProperty, PropertyDescriptor> entry : bodiesResolveContext.getProperties().entrySet()) {
            KtProperty function = entry.getKey();
            PropertyDescriptor propertyDescriptor = entry.getValue();

            for (AnalyzerExtension extension : getPropertyExtensions(propertyDescriptor)) {
                extension.process(propertyDescriptor, function, trace);
            }
        }
    }

    @NotNull
    private List<InlineAnalyzerExtension> getFunctionExtensions(@NotNull FunctionDescriptor functionDescriptor) {
        if (InlineUtil.isInline(functionDescriptor)) {
            return Collections.singletonList(new InlineAnalyzerExtension(reasonableInlineRules, languageVersionSettings));
        }
        return Collections.emptyList();
    }

    @NotNull
    private List<InlineAnalyzerExtension> getPropertyExtensions(@NotNull PropertyDescriptor propertyDescriptor) {
        if (InlineUtil.hasInlineAccessors(propertyDescriptor)) {
            return Collections.singletonList(new InlineAnalyzerExtension(reasonableInlineRules, languageVersionSettings));
        }
        return Collections.emptyList();
    }
}
