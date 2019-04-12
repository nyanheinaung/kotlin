/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolder
import com.intellij.psi.PsiElement
import kotlin.reflect.KProperty

class UserDataProperty<in R : UserDataHolder, T : Any>(val key: Key<T>) {
    operator fun getValue(thisRef: R, desc: KProperty<*>) = thisRef.getUserData(key)

    operator fun setValue(thisRef: R, desc: KProperty<*>, value: T?) = thisRef.putUserData(key, value)
}

class NotNullableUserDataProperty<in R : UserDataHolder, T : Any>(val key: Key<T>, val defaultValue: T) {
    operator fun getValue(thisRef: R, desc: KProperty<*>) = thisRef.getUserData(key) ?: defaultValue

    operator fun setValue(thisRef: R, desc: KProperty<*>, value: T) {
        thisRef.putUserData(key, if (value != defaultValue) value else null)
    }
}

class CopyablePsiUserDataProperty<in R : PsiElement, T : Any>(val key: Key<T>) {
    operator fun getValue(thisRef: R, property: KProperty<*>) = thisRef.getCopyableUserData(key)

    operator fun setValue(thisRef: R, property: KProperty<*>, value: T?) = thisRef.putCopyableUserData(key, value)
}

class NotNullablePsiCopyableUserDataProperty<in R : PsiElement, T : Any>(val key: Key<T>, val defaultValue: T) {
    operator fun getValue(thisRef: R, property: KProperty<*>) = thisRef.getCopyableUserData(key) ?: defaultValue

    operator fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        thisRef.putCopyableUserData(key, if (value != defaultValue) value else null)
    }
}