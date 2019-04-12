/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.annotations.jvm;

import java.lang.annotation.*;

/**
 * Annotate anything of type descendant from Iterable or Iterator with @ReadOnly if the underlying reference is not intended for mutation.
 * Note that such a reference is not guaranteed to be immutable. It may refer to an object mutated elsewhere.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface ReadOnly {
}
