/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.refactoring.changeSignature.ui;

import com.intellij.openapi.ui.ComboBoxTableRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.changeSignature.ParameterTableModelItemBase;
import com.intellij.util.ui.ColumnInfo;
import org.jetbrains.kotlin.idea.KotlinFileType;
import org.jetbrains.kotlin.idea.refactoring.KotlinRefactoringBundle;
import org.jetbrains.kotlin.idea.refactoring.changeSignature.KotlinMethodDescriptor;
import org.jetbrains.kotlin.idea.refactoring.changeSignature.KotlinParameterInfo;
import org.jetbrains.kotlin.idea.refactoring.changeSignature.KotlinValVar;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class KotlinPrimaryConstructorParameterTableModel extends KotlinCallableParameterTableModel {
    public KotlinPrimaryConstructorParameterTableModel(KotlinMethodDescriptor methodDescriptor, PsiElement typeContext, PsiElement defaultValueContext) {
        super(methodDescriptor,
              typeContext,
              defaultValueContext,
              new ValVarColumn(),
              new NameColumn(typeContext.getProject()),
              new TypeColumn(typeContext.getProject(), KotlinFileType.INSTANCE),
              new DefaultValueColumn<KotlinParameterInfo, ParameterTableModelItemBase<KotlinParameterInfo>>(typeContext.getProject(), KotlinFileType.INSTANCE));
    }

    public static boolean isValVarColumn(ColumnInfo column) {
        return column instanceof ValVarColumn;
    }

    protected static class ValVarColumn extends ColumnInfoBase<KotlinParameterInfo, ParameterTableModelItemBase<KotlinParameterInfo>, KotlinValVar> {
        public ValVarColumn() {
            super(KotlinRefactoringBundle.message("column.name.val.var"));
        }

        @Override
        public boolean isCellEditable(ParameterTableModelItemBase<KotlinParameterInfo> item) {
            return !item.isEllipsisType() && item.parameter.isNewParameter();
        }

        @Override
        public KotlinValVar valueOf(ParameterTableModelItemBase<KotlinParameterInfo> item) {
            return item.parameter.getValOrVar();
        }

        @Override
        public void setValue(ParameterTableModelItemBase<KotlinParameterInfo> item, KotlinValVar value) {
            item.parameter.setValOrVar(value);
        }

        @Override
        public TableCellRenderer doCreateRenderer(ParameterTableModelItemBase<KotlinParameterInfo> item) {
            return new ComboBoxTableRenderer<KotlinValVar>(KotlinValVar.values());
        }

        @Override
        public TableCellEditor doCreateEditor(ParameterTableModelItemBase<KotlinParameterInfo> item) {
            return new DefaultCellEditor(new JComboBox());
        }

        @Override
        public int getWidth(JTable table) {
            return table.getFontMetrics(table.getFont()).stringWidth(getName()) + 8;
        }
    }
}
