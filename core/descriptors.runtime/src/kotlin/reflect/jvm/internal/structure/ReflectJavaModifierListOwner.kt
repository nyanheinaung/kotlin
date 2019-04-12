/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.structure

import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.load.java.JavaVisibilities
import org.jetbrains.kotlin.load.java.structure.JavaModifierListOwner
import java.lang.reflect.Modifier

interface ReflectJavaModifierListOwner : JavaModifierListOwner {
    val modifiers: Int

    override val isAbstract: Boolean
        get() = Modifier.isAbstract(modifiers)

    override val isStatic: Boolean
        get() = Modifier.isStatic(modifiers)

    override val isFinal: Boolean
        get() = Modifier.isFinal(modifiers)

    override val visibility: Visibility
        get() = modifiers.let { modifiers ->
            when {
                Modifier.isPublic(modifiers) -> Visibilities.PUBLIC
                Modifier.isPrivate(modifiers) -> Visibilities.PRIVATE
                Modifier.isProtected(modifiers) ->
                    if (Modifier.isStatic(modifiers)) JavaVisibilities.PROTECTED_STATIC_VISIBILITY
                    else JavaVisibilities.PROTECTED_AND_PACKAGE
                else -> JavaVisibilities.PACKAGE_VISIBILITY
            }
        }
}
