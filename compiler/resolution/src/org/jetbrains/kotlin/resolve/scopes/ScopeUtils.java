/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.scopes;

import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.PropertyDescriptor;
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor;
import org.jetbrains.kotlin.descriptors.VariableDescriptorWithAccessors;
import org.jetbrains.kotlin.utils.Printer;

public final class ScopeUtils {
    private ScopeUtils() {}

    public static LexicalScope makeScopeForPropertyHeader(
            @NotNull LexicalScope parent,
            @NotNull PropertyDescriptor propertyDescriptor
    ) {
        return new LexicalScopeImpl(parent, propertyDescriptor, false, null, LexicalScopeKind.PROPERTY_HEADER,
                                    // redeclaration on type parameters should be reported early, see: DescriptorResolver.resolvePropertyDescriptor()
                                    LocalRedeclarationChecker.DO_NOTHING.INSTANCE,
                                    handler -> {
                                        for (TypeParameterDescriptor typeParameterDescriptor : propertyDescriptor.getTypeParameters()) {
                                            handler.addClassifierDescriptor(typeParameterDescriptor);
                                        }
                                        return Unit.INSTANCE;
                                    });
    }

    @NotNull
    public static LexicalScope makeScopeForPropertyInitializer(
            @NotNull LexicalScope propertyHeader,
            @NotNull PropertyDescriptor propertyDescriptor
    ) {
        return new LexicalScopeImpl(propertyHeader, propertyDescriptor, false, null, LexicalScopeKind.PROPERTY_INITIALIZER_OR_DELEGATE);
    }

    @NotNull
    public static LexicalScope makeScopeForDelegateConventionFunctions(
            @NotNull LexicalScope parent,
            @NotNull VariableDescriptorWithAccessors variableDescriptor
    ) {
        // todo: very strange scope!
        return new LexicalScopeImpl(parent, variableDescriptor, true, variableDescriptor.getExtensionReceiverParameter(),
                                    LexicalScopeKind.PROPERTY_DELEGATE_METHOD
        );
    }

    // TestOnly
    @NotNull
    public static String printStructure(@Nullable MemberScope scope) {
        StringBuilder out = new StringBuilder();
        Printer p = new Printer(out);
        if (scope == null) {
            p.println("null");
        }
        else {
            scope.printScopeStructure(p);
        }
        return out.toString();
    }
}
