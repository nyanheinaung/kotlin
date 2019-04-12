/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.refactoring.changeSignature.ui;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.changeSignature.ParameterTableModelItemBase;
import com.intellij.refactoring.ui.StringTableCellEditor;
import com.intellij.ui.BooleanTableCellRenderer;
import com.intellij.util.ui.ColumnInfo;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.idea.KotlinFileType;
import org.jetbrains.kotlin.idea.refactoring.changeSignature.KotlinMethodDescriptor;
import org.jetbrains.kotlin.idea.refactoring.changeSignature.KotlinParameterInfo;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class KotlinFunctionParameterTableModel extends KotlinCallableParameterTableModel {
    public KotlinFunctionParameterTableModel(KotlinMethodDescriptor methodDescriptor, PsiElement typeContext, PsiElement defaultValueContext) {
        super(methodDescriptor,
              typeContext,
              defaultValueContext,
              new NameColumn(typeContext.getProject()),
              new TypeColumn(typeContext.getProject(), KotlinFileType.INSTANCE),
              new DefaultValueColumn<KotlinParameterInfo, ParameterTableModelItemBase<KotlinParameterInfo>>(typeContext.getProject(),
                                                                                                            KotlinFileType.INSTANCE),
              new ReceiverColumn(typeContext.getProject(), methodDescriptor));
    }

    @Override
    public void removeRow(int idx) {
        if (getRowValue(idx).parameter == getReceiver()) {
            setReceiver(null);
        }
        super.removeRow(idx);
    }

    @Override
    @Nullable
    public KotlinParameterInfo getReceiver() {
        return ((ReceiverColumn)getColumnInfos()[getColumnCount() - 1]).receiver;
    }

    public void setReceiver(@Nullable KotlinParameterInfo receiver) {
        ((ReceiverColumn)getColumnInfos()[getColumnCount() - 1]).receiver = receiver;
    }

    public static boolean isReceiverColumn(ColumnInfo column) {
        return column instanceof ReceiverColumn;
    }

    protected static class ReceiverColumn<TableItem extends ParameterTableModelItemBase<KotlinParameterInfo>>
            extends ColumnInfoBase<KotlinParameterInfo, TableItem, Boolean> {
        private final Project project;
        @Nullable
        private KotlinParameterInfo receiver;

        public ReceiverColumn(Project project, @Nullable KotlinMethodDescriptor methodDescriptor) {
            super("Receiver:");
            this.project = project;
            this.receiver = methodDescriptor != null ? methodDescriptor.getReceiver() : null;
        }

        @Override
        public Boolean valueOf(TableItem item) {
            return item.parameter == receiver;
        }

        @Override
        public void setValue(TableItem item, Boolean value) {
            if (value == null) return;
            receiver = value ? item.parameter : null;
        }

        @Override
        public boolean isCellEditable(TableItem pParameterTableModelItemBase) {
            return true;
        }

        @Override
        public TableCellRenderer doCreateRenderer(TableItem item) {
            return new BooleanTableCellRenderer();
        }

        @Override
        public TableCellEditor doCreateEditor(TableItem o) {
            return new StringTableCellEditor(project);
        }
    }
}
