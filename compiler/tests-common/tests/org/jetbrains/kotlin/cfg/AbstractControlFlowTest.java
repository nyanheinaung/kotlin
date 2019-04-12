/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.cfg.pseudocode.PseudocodeImpl;
import org.jetbrains.kotlin.cfg.pseudocode.instructions.Instruction;
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionImpl;
import org.jetbrains.kotlin.resolve.BindingContext;

import java.util.*;

public abstract class AbstractControlFlowTest extends AbstractPseudocodeTest {

    @Override
    protected void dumpInstructions(
            @NotNull PseudocodeImpl pseudocode,
            @NotNull StringBuilder out,
            @NotNull BindingContext bindingContext
    ) {
        int nextInstructionsColumnWidth = countNextInstructionsColumnWidth(pseudocode.getInstructionsIncludingDeadCode());

        dumpInstructions(pseudocode, out, (instruction, next, prev) -> {
            StringBuilder result = new StringBuilder();
            Collection<Instruction> nextInstructions = instruction.getNextInstructions();

            if (!sameContents(next, nextInstructions)) {
                result.append("    NEXT:").append(
                        String.format("%1$-" + nextInstructionsColumnWidth + "s", formatInstructionList(nextInstructions)));
            }
            Collection<Instruction> previousInstructions = instruction.getPreviousInstructions();
            if (!sameContents(prev, previousInstructions)) {
                result.append("    PREV:").append(formatInstructionList(previousInstructions));
            }
            return result.toString();
        });
    }

    private static String formatInstructionList(Collection<Instruction> instructions) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (Iterator<Instruction> iterator = instructions.iterator(); iterator.hasNext(); ) {
            Instruction instruction = iterator.next();
            String instructionText = instruction.toString();
            String[] parts = instructionText.split("\n");
            if (parts.length > 1) {
                StringBuilder instructionSb = new StringBuilder();
                for (String part : parts) {
                    instructionSb.append(part.trim()).append(' ');
                }
                if (instructionSb.toString().length() > 30) {
                    sb.append(instructionSb.substring(0, 28)).append("..)");
                }
                else {
                    sb.append(instructionSb);
                }
            }
            else {
                sb.append(instruction);
            }
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(']');
        return sb.toString();
    }


    private static int countNextInstructionsColumnWidth(List<Instruction> instructions) {
        int maxWidth = 0;
        for (Instruction instruction : instructions) {
            String instructionListText = formatInstructionList(instruction.getNextInstructions());
            if (instructionListText.length() > maxWidth) {
                maxWidth = instructionListText.length();
            }
        }
        return maxWidth;
    }

    private static boolean sameContents(@Nullable Instruction natural, Collection<Instruction> actual) {
        if (natural == null) {
            return actual.isEmpty();
        }
        return Collections.singleton(natural).equals(new HashSet<>(actual));
    }

    @Override
    protected void checkPseudocode(PseudocodeImpl pseudocode) {
        //check edges directions
        Collection<Instruction> instructions = pseudocode.getInstructionsIncludingDeadCode();
        for (Instruction instruction : instructions) {
            if (!((InstructionImpl)instruction).getMarkedAsDead()) {
                for (Instruction nextInstruction : instruction.getNextInstructions()) {
                    assertTrue("instruction '" + instruction + "' has '" + nextInstruction + "' among next instructions list, but not vice versa",
                               nextInstruction.getPreviousInstructions().contains(instruction));
                }
                for (Instruction prevInstruction : instruction.getPreviousInstructions()) {
                    assertTrue("instruction '" + instruction + "' has '" + prevInstruction + "' among previous instructions list, but not vice versa",
                               prevInstruction.getNextInstructions().contains(instruction));
                }
            }
        }
    }
}
