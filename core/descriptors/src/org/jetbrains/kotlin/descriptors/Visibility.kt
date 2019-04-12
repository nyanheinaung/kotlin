/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue

abstract class Visibility protected constructor(
        val name: String,
        val isPublicAPI: Boolean
) {
    /**
     * @param receiver can be used to determine callee accessibility for some special receiver value
     *
     * 'null'-value basically means that receiver is absent in current call
     *
     * In case if it's needed to perform basic checks ignoring ones considering receiver (e.g. when checks happen beyond any call),
     * special value Visibilities.ALWAYS_SUITABLE_RECEIVER should be used.
     * If it's needed to determine whether visibility accepts any receiver, Visibilities.IRRELEVANT_RECEIVER should be used.
     *
     * NB: Currently Visibilities.IRRELEVANT_RECEIVER has the same effect as 'null'
     *
     * Also it's important that implementation that take receiver into account do aware about these special values.
     */
    abstract fun isVisible(receiver: ReceiverValue?, what: DeclarationDescriptorWithVisibility, from: DeclarationDescriptor): Boolean

    /**
     * True, if it makes sense to check this visibility in imports and not import inaccessible declarations with such visibility.
     * Hint: return true, if this visibility can be checked on file's level.
     * Examples:
     * it returns false for PROTECTED because protected members of classes can be imported to be used in subclasses of their containers,
     * so when we are looking at the import, we don't know whether it is legal somewhere in this file or not.
     * it returns true for INTERNAL, because an internal declaration is either visible everywhere in a file, or invisible everywhere in the same file.
     * it returns true for PRIVATE, because there's no point in importing privates: they are inaccessible unless their short name is
     * already available without an import
     */
    abstract fun mustCheckInImports(): Boolean

    /**
     * @return null if the answer is unknown
     */
    protected open fun compareTo(visibility: Visibility): Int? {
        return Visibilities.compareLocal(this, visibility)
    }

    open val displayName: String
        get() = name

    override final fun toString() = displayName

    open fun normalize(): Visibility = this

    // Should be overloaded in Java visibilities
    open fun effectiveVisibility(descriptor: DeclarationDescriptor, checkPublishedApi: Boolean) = effectiveVisibility(normalize(), descriptor, checkPublishedApi)
}
