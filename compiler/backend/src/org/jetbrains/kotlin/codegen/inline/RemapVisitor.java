/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.codegen.StackValue;
import org.jetbrains.org.objectweb.asm.Label;
import org.jetbrains.org.objectweb.asm.MethodVisitor;
import org.jetbrains.org.objectweb.asm.Opcodes;
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter;
import org.jetbrains.org.objectweb.asm.tree.FieldInsnNode;

import static org.jetbrains.kotlin.codegen.inline.InlineCodegenUtilsKt.CAPTURED_FIELD_FOLD_PREFIX;

public class RemapVisitor extends SkipMaxAndEndVisitor {
    private final LocalVarRemapper remapper;
    private final FieldRemapper nodeRemapper;
    private final InstructionAdapter instructionAdapter;

    public RemapVisitor(
            @NotNull MethodVisitor mv,
            @NotNull LocalVarRemapper remapper,
            @NotNull FieldRemapper nodeRemapper
    ) {
        super(mv);
        this.instructionAdapter = new InstructionAdapter(mv);
        this.remapper = remapper;
        this.nodeRemapper = nodeRemapper;
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        remapper.visitIincInsn(var, increment, mv);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        remapper.visitVarInsn(opcode, var, instructionAdapter);
    }

    @Override
    public void visitLocalVariable(
            @NotNull String name, @NotNull String desc, String signature, @NotNull Label start, @NotNull Label end, int index
    ) {
        remapper.visitLocalVariable(name, desc, signature, start, end, index, mv);
    }

    @Override
    public void visitFieldInsn(int opcode, @NotNull String owner, @NotNull String name, @NotNull String desc) {
        if (name.startsWith(CAPTURED_FIELD_FOLD_PREFIX) &&
            (nodeRemapper instanceof RegeneratedLambdaFieldRemapper || nodeRemapper.isRoot())) {
            FieldInsnNode fin = new FieldInsnNode(opcode, owner, name, desc);
            StackValue inline = nodeRemapper.getFieldForInline(fin, null);
            assert inline != null : "Captured field should have not null stackValue " + fin;
            if (Opcodes.PUTSTATIC == opcode) {
                inline.store(StackValue.onStack(inline.type, inline.kotlinType), this);
            }
            else {
                inline.put(inline.type, inline.kotlinType, this);
            }
            return;
        }
        super.visitFieldInsn(opcode, owner, name, desc);
    }
}
