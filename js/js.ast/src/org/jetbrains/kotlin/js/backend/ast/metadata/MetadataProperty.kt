/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.backend.ast.metadata

import kotlin.reflect.KProperty

class MetadataProperty<in T : HasMetadata, R>(val default: R) {
    operator fun getValue(thisRef: T, desc: KProperty<*>): R {
        if (!thisRef.hasData(desc.name)) return default
        return thisRef.getData<R>(desc.name)
    }

    operator fun setValue(thisRef: T, desc: KProperty<*>, value: R) {
        if (value == default) {
            thisRef.removeData(desc.name)
        }
        else {
            thisRef.setData(desc.name, value)
        }
    }
}
