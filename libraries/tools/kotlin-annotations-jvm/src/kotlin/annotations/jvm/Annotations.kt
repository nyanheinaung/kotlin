/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.annotations.jvm

/**
 * Contains the list of possible migration statuses.
 */
public enum class MigrationStatus {
    IGNORE,
    WARN,
    @Deprecated("experimental feature")
    STRICT
}

/**
 * This meta-annotation is intended for user nullability annotations with JSR-305 type qualifiers. Behaviour of meta-annotated
 * nullability annotations can be controlled via compilation flag.
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
public annotation class UnderMigration(val status: MigrationStatus)
