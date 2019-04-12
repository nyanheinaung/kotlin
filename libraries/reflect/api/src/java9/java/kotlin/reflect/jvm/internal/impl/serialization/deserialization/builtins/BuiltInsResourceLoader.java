/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.impl.serialization.deserialization.builtins;

import kotlin.Unit;

import java.io.IOException;
import java.io.InputStream;

public class BuiltInsResourceLoader {
    public InputStream loadResource(String path) throws IOException {
        Module stdlib = Unit.class.getModule();
        return stdlib != null ? stdlib.getResourceAsStream(path) : null;
    }
}
