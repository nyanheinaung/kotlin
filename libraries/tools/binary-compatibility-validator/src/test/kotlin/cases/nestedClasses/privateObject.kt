/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package cases.nestedClasses

private object PrivateObject {

    public object ObjPublic
    internal object ObjInternal
    private object ObjPrivate

    public class NestedPublic
    internal class NestedInternal
    private class NestedPrivate

    public interface NestedPublicInterface
    internal interface NestedInternalInterface
    private interface NestedPrivateInterface
}

