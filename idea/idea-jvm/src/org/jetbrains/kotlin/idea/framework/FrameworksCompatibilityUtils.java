/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.framework;

import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryKind;
import com.intellij.openapi.roots.ui.configuration.libraries.LibraryPresentationManager;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class FrameworksCompatibilityUtils {
    private FrameworksCompatibilityUtils() {
    }

    public static void suggestRemoveIncompatibleFramework(
            @NotNull ModifiableRootModel rootModel,
            @NotNull Set<? extends LibraryKind> frameworkLibraryKinds,
            @NotNull String presentableName
    ) {
        List<OrderEntry> existingEntries = new ArrayList<OrderEntry>();

        for (OrderEntry entry : rootModel.getOrderEntries()) {
            if (!(entry instanceof LibraryOrderEntry)) continue;
            Library library = ((LibraryOrderEntry)entry).getLibrary();
            if (library == null) continue;

            for (LibraryKind kind : frameworkLibraryKinds) {
                if (LibraryPresentationManager.getInstance().isLibraryOfKind(Arrays.asList(library.getFiles(OrderRootType.CLASSES)), kind)) {
                    existingEntries.add(entry);
                }
            }
        }

        removeWithConfirm(rootModel, existingEntries,
                          String.format("Current module is already configured with '%s' framework.\nDo you want to remove it?",
                                        presentableName),
                          "Framework Conflict");
    }

    private static void removeWithConfirm(ModifiableRootModel rootModel, List<OrderEntry> orderEntries, String message, String title) {
        if (!orderEntries.isEmpty()) {
            int result = Messages.showYesNoDialog(message, title, Messages.getWarningIcon());


            if (result == 0) {
                for (OrderEntry entry : orderEntries) {
                    rootModel.removeOrderEntry(entry);
                }
            }
        }
    }
}
