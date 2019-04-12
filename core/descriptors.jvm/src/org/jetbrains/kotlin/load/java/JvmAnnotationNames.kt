/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java

import org.jetbrains.kotlin.name.FqName

val NULLABLE_ANNOTATIONS = listOf(
    JvmAnnotationNames.JETBRAINS_NULLABLE_ANNOTATION,
    FqName("androidx.annotation.Nullable"),
    FqName("android.support.annotation.Nullable"),
    FqName("android.annotation.Nullable"),
    FqName("com.android.annotations.Nullable"),
    FqName("org.eclipse.jdt.annotation.Nullable"),
    FqName("org.checkerframework.checker.nullness.qual.Nullable"),
    FqName("javax.annotation.Nullable"),
    FqName("javax.annotation.CheckForNull"),
    FqName("edu.umd.cs.findbugs.annotations.CheckForNull"),
    FqName("edu.umd.cs.findbugs.annotations.Nullable"),
    FqName("edu.umd.cs.findbugs.annotations.PossiblyNull"),
    FqName("io.reactivex.annotations.Nullable")
)

val JAVAX_NONNULL_ANNOTATION = FqName("javax.annotation.Nonnull")
val JAVAX_CHECKFORNULL_ANNOTATION = FqName("javax.annotation.CheckForNull")

val NOT_NULL_ANNOTATIONS = listOf(
    JvmAnnotationNames.JETBRAINS_NOT_NULL_ANNOTATION,
    FqName("edu.umd.cs.findbugs.annotations.NonNull"),
    FqName("androidx.annotation.NonNull"),
    FqName("android.support.annotation.NonNull"),
    FqName("android.annotation.NonNull"),
    FqName("com.android.annotations.NonNull"),
    FqName("org.eclipse.jdt.annotation.NonNull"),
    FqName("org.checkerframework.checker.nullness.qual.NonNull"),
    FqName("lombok.NonNull"),
    FqName("io.reactivex.annotations.NonNull")
)

val COMPATQUAL_NULLABLE_ANNOTATION = FqName("org.checkerframework.checker.nullness.compatqual.NullableDecl")
val COMPATQUAL_NONNULL_ANNOTATION = FqName("org.checkerframework.checker.nullness.compatqual.NonNullDecl")

val ANDROIDX_RECENTLY_NULLABLE_ANNOTATION = FqName("androidx.annotation.RecentlyNullable")
val ANDROIDX_RECENTLY_NON_NULL_ANNOTATION = FqName("androidx.annotation.RecentlyNonNull")

val NULLABILITY_ANNOTATIONS: Set<FqName> = mutableSetOf<FqName>() +
        NULLABLE_ANNOTATIONS +
        JAVAX_NONNULL_ANNOTATION +
        NOT_NULL_ANNOTATIONS +
        COMPATQUAL_NULLABLE_ANNOTATION +
        COMPATQUAL_NONNULL_ANNOTATION +
        ANDROIDX_RECENTLY_NULLABLE_ANNOTATION +
        ANDROIDX_RECENTLY_NON_NULL_ANNOTATION

val READ_ONLY_ANNOTATIONS = listOf(
    JvmAnnotationNames.JETBRAINS_READONLY_ANNOTATION,
    JvmAnnotationNames.READONLY_ANNOTATION
)

val MUTABLE_ANNOTATIONS = listOf(
    JvmAnnotationNames.JETBRAINS_MUTABLE_ANNOTATION,
    JvmAnnotationNames.MUTABLE_ANNOTATION
)
