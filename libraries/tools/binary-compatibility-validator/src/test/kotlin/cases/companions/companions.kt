/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package cases.companions


object PublicClasses {
    class PublicCompanion {
        companion object
    }

    class ProtectedCompanion {
        protected companion object
    }

    abstract class AbstractProtectedCompanion {
        protected companion object
    }

    class InternalCompanion {
        internal companion object
    }

    class PrivateCompanion {
        private companion object
    }
}

object PublicInterfaces {
    interface PublicCompanion {
        companion object
    }

    interface PrivateCompanion {
        private companion object
    }
}



object InternalClasses {
    internal class PublicCompanion {
        companion object
    }

    internal class ProtectedCompanion {
        protected companion object
    }

    internal abstract class AbstractProtectedCompanion {
        protected companion object
    }

    internal class InternalCompanion {
        internal companion object
    }

    internal class PrivateCompanion {
        private companion object
    }
}

object InternalInterfaces {
    internal interface PublicCompanion {
        companion object
    }

    internal interface PrivateCompanion {
        private companion object
    }
}


object PrivateClasses {
    private class PublicCompanion {
        companion object
    }

    private class ProtectedCompanion {
        protected companion object
    }

    private abstract class AbstractProtectedCompanion {
        protected companion object
    }

    private class InternalCompanion {
        internal companion object
    }

    private class PrivateCompanion {
        private companion object
    }
}

object PrivateInterfaces {
    private interface PublicCompanion {
        companion object
    }

    private interface PrivateCompanion {
        private companion object
    }
}

