/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.refactoring.changeSignature.ui;

import com.intellij.psi.PsiElement;
import com.intellij.refactoring.changeSignature.ParameterTableModelItemBase;
import org.jetbrains.kotlin.idea.KotlinFileType;
import org.jetbrains.kotlin.idea.refactoring.changeSignature.KotlinMethodDescriptor;
import org.jetbrains.kotlin.idea.refactoring.changeSignature.KotlinParameterInfo;

public class KotlinSecondaryConstructorParameterTableModel extends KotlinCallableParameterTableModel {
    public KotlinSecondaryConstructorParameterTableModel(KotlinMethodDescriptor methodDescriptor, PsiElement typeContext, PsiElement defaultValueContext) {
        super(methodDescriptor,
              typeContext,
              defaultValueContext,
              new NameColumn(typeContext.getProject()),
              new TypeColumn(typeContext.getProject(), KotlinFileType.INSTANCE),
              new DefaultValueColumn<KotlinParameterInfo, ParameterTableModelItemBase<KotlinParameterInfo>>(typeContext.getProject(), KotlinFileType.INSTANCE));
    }
}
