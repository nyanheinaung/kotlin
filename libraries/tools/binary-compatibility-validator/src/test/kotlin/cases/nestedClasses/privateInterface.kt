/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package cases.nestedClasses

private interface PrivateInterface {
    public object ObjPublic
    private object ObjPrivate

    public class NestedPublic
    private class NestedPrivate

    public interface NestedPublicInterface
    private interface NestedPrivateInterface

}

