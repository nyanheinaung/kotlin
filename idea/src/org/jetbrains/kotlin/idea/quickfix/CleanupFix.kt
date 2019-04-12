/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.quickfix

import com.intellij.codeInsight.intention.IntentionAction

/**
 * Marker interface for quickfixes that can be used as part of the "Cleanup Code" action. The diagnostics
 * that produce these quickfixes need to be added to KotlinCleanupInspection.cleanupDiagnosticsFactories.
 */
interface CleanupFix : IntentionAction {
}
// TODO(yole): add isSafeToApply() method here to get rid of filtering by diagnostics factories in
// KotlinCleanupInspection
