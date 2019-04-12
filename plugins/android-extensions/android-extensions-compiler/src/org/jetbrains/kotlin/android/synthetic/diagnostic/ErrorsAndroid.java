/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.synthetic.diagnostic;

import com.intellij.psi.PsiElement;
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory0;
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory1;
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory2;
import org.jetbrains.kotlin.diagnostics.Errors;
import org.jetbrains.kotlin.psi.KtAnnotationEntry;
import org.jetbrains.kotlin.psi.KtClassOrObject;
import org.jetbrains.kotlin.psi.KtElement;
import org.jetbrains.kotlin.psi.KtExpression;
import org.jetbrains.kotlin.types.KotlinType;

import static org.jetbrains.kotlin.diagnostics.Severity.ERROR;
import static org.jetbrains.kotlin.diagnostics.Severity.WARNING;

public interface ErrorsAndroid {
    DiagnosticFactory1<KtExpression, String> SYNTHETIC_INVALID_WIDGET_TYPE = DiagnosticFactory1.create(WARNING);
    DiagnosticFactory1<KtExpression, String> SYNTHETIC_UNRESOLVED_WIDGET_TYPE = DiagnosticFactory1.create(WARNING);
    DiagnosticFactory0<KtExpression> SYNTHETIC_DEPRECATED_PACKAGE = DiagnosticFactory0.create(WARNING);
    DiagnosticFactory0<KtExpression> UNSAFE_CALL_ON_PARTIALLY_DEFINED_RESOURCE = DiagnosticFactory0.create(WARNING);

    DiagnosticFactory0<PsiElement> PARCELABLE_SHOULD_BE_CLASS = DiagnosticFactory0.create(ERROR);
    DiagnosticFactory0<PsiElement> PARCELABLE_DELEGATE_IS_NOT_ALLOWED = DiagnosticFactory0.create(ERROR);
    DiagnosticFactory0<PsiElement> PARCELABLE_SHOULD_NOT_BE_ENUM_CLASS = DiagnosticFactory0.create(ERROR);
    DiagnosticFactory0<PsiElement> PARCELABLE_SHOULD_BE_INSTANTIABLE = DiagnosticFactory0.create(ERROR);
    DiagnosticFactory0<PsiElement> PARCELABLE_CANT_BE_INNER_CLASS = DiagnosticFactory0.create(ERROR);
    DiagnosticFactory0<PsiElement> PARCELABLE_CANT_BE_LOCAL_CLASS = DiagnosticFactory0.create(ERROR);
    DiagnosticFactory0<PsiElement> NO_PARCELABLE_SUPERTYPE = DiagnosticFactory0.create(ERROR);
    DiagnosticFactory0<PsiElement> PARCELABLE_SHOULD_HAVE_PRIMARY_CONSTRUCTOR = DiagnosticFactory0.create(ERROR);
    DiagnosticFactory0<PsiElement> PARCELABLE_PRIMARY_CONSTRUCTOR_IS_EMPTY = DiagnosticFactory0.create(WARNING);
    DiagnosticFactory0<PsiElement> PARCELABLE_CONSTRUCTOR_PARAMETER_SHOULD_BE_VAL_OR_VAR = DiagnosticFactory0.create(ERROR);
    DiagnosticFactory0<PsiElement> PROPERTY_WONT_BE_SERIALIZED = DiagnosticFactory0.create(WARNING);
    DiagnosticFactory0<PsiElement> OVERRIDING_WRITE_TO_PARCEL_IS_NOT_ALLOWED = DiagnosticFactory0.create(ERROR);
    DiagnosticFactory0<PsiElement> CREATOR_DEFINITION_IS_NOT_ALLOWED = DiagnosticFactory0.create(ERROR);
    DiagnosticFactory0<PsiElement> PARCELABLE_TYPE_NOT_SUPPORTED = DiagnosticFactory0.create(ERROR);
    DiagnosticFactory0<PsiElement> PARCELER_SHOULD_BE_OBJECT = DiagnosticFactory0.create(ERROR);
    DiagnosticFactory2<PsiElement, KotlinType, KotlinType> PARCELER_TYPE_INCOMPATIBLE = DiagnosticFactory2.create(ERROR);
    DiagnosticFactory0<PsiElement> DUPLICATING_TYPE_PARCELERS = DiagnosticFactory0.create(ERROR);
    DiagnosticFactory1<PsiElement, KtClassOrObject> REDUNDANT_TYPE_PARCELER = DiagnosticFactory1.create(WARNING);
    DiagnosticFactory1<PsiElement, KtClassOrObject> CLASS_SHOULD_BE_PARCELIZE = DiagnosticFactory1.create(ERROR);

    DiagnosticFactory0<PsiElement> INAPPLICABLE_IGNORED_ON_PARCEL = DiagnosticFactory0.create(WARNING);
    DiagnosticFactory0<PsiElement> INAPPLICABLE_IGNORED_ON_PARCEL_CONSTRUCTOR_PROPERTY = DiagnosticFactory0.create(WARNING);

    @SuppressWarnings("UnusedDeclaration")
    Object _initializer = new Object() {
        {
            Errors.Initializer.initializeFactoryNames(ErrorsAndroid.class);
        }
    };

}
