/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.when;

import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.codegen.ClassBuilder;
import org.jetbrains.kotlin.codegen.WriteAnnotationUtilKt;
import org.jetbrains.kotlin.codegen.state.GenerationState;
import org.jetbrains.kotlin.psi.KtFile;
import org.jetbrains.kotlin.resolve.constants.EnumValue;
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin;
import org.jetbrains.org.objectweb.asm.MethodVisitor;
import org.jetbrains.org.objectweb.asm.Type;
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter;

import java.util.List;
import java.util.Map;

import static org.jetbrains.kotlin.resolve.jvm.AsmTypes.OBJECT_TYPE;
import static org.jetbrains.org.objectweb.asm.Opcodes.*;

public class MappingClassesForWhenByEnumCodegen {
    public static final String MAPPINGS_FIELD_DESCRIPTOR = Type.getDescriptor(int[].class);
    private final GenerationState state;

    public MappingClassesForWhenByEnumCodegen(@NotNull GenerationState state) {
        this.state = state;
    }

    public void generate(@NotNull List<WhenByEnumsMapping> mappings, @NotNull Type mappingsClass, @NotNull KtFile srcFile) {
        ClassBuilder cb = state.getFactory().newVisitor(JvmDeclarationOrigin.NO_ORIGIN, mappingsClass, srcFile);
        cb.defineClass(
                srcFile,
                state.getClassFileVersion(),
                ACC_PUBLIC | ACC_FINAL | ACC_SUPER | ACC_SYNTHETIC,
                mappingsClass.getInternalName(),
                null,
                OBJECT_TYPE.getInternalName(),
                ArrayUtil.EMPTY_STRING_ARRAY
        );

        generateFields(cb, mappings);
        generateInitialization(cb, mappings);

        WriteAnnotationUtilKt.writeSyntheticClassMetadata(cb, state);

        cb.done();
    }

    private static void generateFields(@NotNull ClassBuilder cb, @NotNull List<WhenByEnumsMapping> mappings) {
        for (WhenByEnumsMapping mapping : mappings) {
            cb.newField(
                    JvmDeclarationOrigin.NO_ORIGIN,
                    ACC_STATIC | ACC_PUBLIC | ACC_FINAL | ACC_SYNTHETIC,
                    mapping.getFieldName(),
                    MAPPINGS_FIELD_DESCRIPTOR,
                    null, null
            );
        }
    }

    private void generateInitialization(@NotNull ClassBuilder cb, @NotNull List<WhenByEnumsMapping> mappings) {
        MethodVisitor mv = cb.newMethod(
                JvmDeclarationOrigin.NO_ORIGIN,
                ACC_STATIC | ACC_SYNTHETIC, "<clinit>", "()V", null, ArrayUtil.EMPTY_STRING_ARRAY
        );

        mv.visitCode();

        InstructionAdapter v = new InstructionAdapter(mv);

        for (WhenByEnumsMapping mapping : mappings) {
            generateInitializationForMapping(cb, v, mapping);
        }

        v.areturn(Type.VOID_TYPE);

        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }

    private void generateInitializationForMapping(
            @NotNull ClassBuilder cb,
            @NotNull InstructionAdapter v,
            @NotNull WhenByEnumsMapping mapping
    ) {
        Type enumType = state.getTypeMapper().mapClass(mapping.getEnumClassDescriptor());

        v.invokestatic(enumType.getInternalName(), "values", Type.getMethodDescriptor(Type.getType("[" + enumType.getDescriptor())), false);
        v.arraylength();

        v.newarray(Type.INT_TYPE);
        v.putstatic(cb.getThisName(), mapping.getFieldName(), MAPPINGS_FIELD_DESCRIPTOR);

        for (Map.Entry<EnumValue, Integer> item : mapping.enumValuesToIntMapping()) {
            EnumValue enumValue = item.getKey();
            int mappedValue = item.getValue();

            v.getstatic(cb.getThisName(), mapping.getFieldName(), MAPPINGS_FIELD_DESCRIPTOR);
            v.getstatic(enumType.getInternalName(), enumValue.getEnumEntryName().asString(), enumType.getDescriptor());
            v.invokevirtual(enumType.getInternalName(), "ordinal", Type.getMethodDescriptor(Type.INT_TYPE), false);
            v.iconst(mappedValue);
            v.astore(Type.INT_TYPE);
        }
    }
}
