/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.annotations.jvm;

import java.lang.annotation.*;

/**
 * Annotate anything of type descendant from Iterable or Iterator with @Mutable if the corresponding collection/iterator
 * is allowed to be mutated.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface Mutable {
}
