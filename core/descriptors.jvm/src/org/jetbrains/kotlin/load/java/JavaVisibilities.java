/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.*;
import org.jetbrains.kotlin.resolve.DescriptorUtils;
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue;

public class JavaVisibilities {
    private JavaVisibilities() {
    }

    @NotNull
    public static final Visibility PACKAGE_VISIBILITY = new Visibility("package", false) {
        @Override
        public boolean isVisible(@Nullable ReceiverValue receiver, @NotNull DeclarationDescriptorWithVisibility what, @NotNull DeclarationDescriptor from) {
            return areInSamePackage(what, from);
        }

        @Override
        public boolean mustCheckInImports() {
            return true;
        }

        @Override
        protected Integer compareTo(@NotNull Visibility visibility) {
            if (this == visibility) return 0;
            if (Visibilities.isPrivate(visibility)) return 1;
            return -1;
        }

        @NotNull
        @Override
        public String getDisplayName() {
            return "public/*package*/";
        }

        @NotNull
        @Override
        public Visibility normalize() {
            return Visibilities.PROTECTED;
        }

        @NotNull
        @Override
        public EffectiveVisibility effectiveVisibility(@NotNull DeclarationDescriptor classDescriptor, boolean checkPublishedApi) {
            return EffectiveVisibility.PackagePrivate.INSTANCE;
        }
    };

    @NotNull
    public static final Visibility PROTECTED_STATIC_VISIBILITY = new Visibility("protected_static", true) {
        @Override
        public boolean isVisible(@Nullable ReceiverValue receiver, @NotNull DeclarationDescriptorWithVisibility what, @NotNull DeclarationDescriptor from) {
            return isVisibleForProtectedAndPackage(receiver, what, from);
        }

        @Override
        public boolean mustCheckInImports() {
            return false;
        }

        @NotNull
        @Override
        public String getDisplayName() {
            return "protected/*protected static*/";
        }

        @NotNull
        @Override
        public Visibility normalize() {
            return Visibilities.PROTECTED;
        }
    };

    @NotNull
    public static final Visibility PROTECTED_AND_PACKAGE = new Visibility("protected_and_package", true) {
        @Override
        public boolean isVisible(@Nullable ReceiverValue receiver, @NotNull DeclarationDescriptorWithVisibility what, @NotNull DeclarationDescriptor from) {
            return isVisibleForProtectedAndPackage(receiver, what, from);
        }

        @Override
        public boolean mustCheckInImports() {
            return false;
        }

        @Override
        protected Integer compareTo(@NotNull Visibility visibility) {
            if (this == visibility) return 0;
            if (visibility == Visibilities.INTERNAL) return null;
            if (Visibilities.isPrivate(visibility)) return 1;
            return -1;
        }

        @NotNull
        @Override
        public String getDisplayName() {
            return "protected/*protected and package*/";
        }

        @NotNull
        @Override
        public Visibility normalize() {
            return Visibilities.PROTECTED;
        }
    };

    private static boolean isVisibleForProtectedAndPackage(
            @Nullable ReceiverValue receiver,
            @NotNull DeclarationDescriptorWithVisibility what,
            @NotNull DeclarationDescriptor from
    ) {
        if (areInSamePackage(DescriptorUtils.unwrapFakeOverrideToAnyDeclaration(what), from)) {
            return true;
        }

        return Visibilities.PROTECTED.isVisible(receiver, what, from);
    }

    private static boolean areInSamePackage(@NotNull DeclarationDescriptor first, @NotNull DeclarationDescriptor second) {
        PackageFragmentDescriptor whatPackage = DescriptorUtils.getParentOfType(first, PackageFragmentDescriptor.class, false);
        PackageFragmentDescriptor fromPackage = DescriptorUtils.getParentOfType(second, PackageFragmentDescriptor.class, false);
        return fromPackage != null && whatPackage != null && whatPackage.getFqName().equals(fromPackage.getFqName());
    }
}
