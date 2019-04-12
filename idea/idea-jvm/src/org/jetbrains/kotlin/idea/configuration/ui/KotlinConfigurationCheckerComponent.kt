/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.configuration.ui

import com.intellij.ProjectTopics
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationsConfiguration
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.externalSystem.service.project.manage.ProjectDataImportListener
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootEvent
import com.intellij.openapi.roots.ModuleRootListener
import com.intellij.openapi.startup.StartupManager
import org.jetbrains.kotlin.idea.configuration.checkHideNonConfiguredNotifications
import org.jetbrains.kotlin.idea.configuration.getModulesWithKotlinFiles
import org.jetbrains.kotlin.idea.configuration.notifyOutdatedBundledCompilerIfNecessary
import org.jetbrains.kotlin.idea.configuration.showConfigureKotlinNotificationIfNeeded
import org.jetbrains.kotlin.idea.configuration.ui.notifications.notifyKotlinStyleUpdateIfNeeded
import org.jetbrains.kotlin.idea.project.getAndCacheLanguageLevelByDependencies
import org.jetbrains.kotlin.idea.versions.collectModulesWithOutdatedRuntime
import org.jetbrains.kotlin.idea.versions.findOutdatedKotlinLibraries
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class KotlinConfigurationCheckerComponent(val project: Project) : ProjectComponent {
    private val syncDepth = AtomicInteger()

    @Volatile
    private var notificationPostponed = false

    private val moduleRootListener = object : ModuleRootListener {

        private val checkInProgress = AtomicBoolean(false)

        private val modulesUpdatedDuringCheck = AtomicBoolean(false)

        override fun rootsChanged(event: ModuleRootEvent) {
            try {
                modulesUpdatedDuringCheck.set(true)
                // forbid running multiple checks in parallel
                if (!checkInProgress.compareAndSet(false, true)) return

                // if during the execution of the current check the module list has updated, we should re-run this check
                do {
                    modulesUpdatedDuringCheck.set(false)
                    if (!project.isInitialized) return

                    if (notificationPostponed && !isSyncing) {
                        DumbService.getInstance(project).runWhenSmart {
                            if (!isSyncing) {
                                notificationPostponed = false

                                val excludeModules = collectModulesWithOutdatedRuntime(findOutdatedKotlinLibraries(project))

                                showConfigureKotlinNotificationIfNeeded(
                                    project,
                                    excludeModules
                                )
                            }
                        }
                    }

                    checkHideNonConfiguredNotifications(project)
                } while (
                    modulesUpdatedDuringCheck.get()
                )
            } finally {
                checkInProgress.set(false)
            }
        }
    }

    init {
        NotificationsConfiguration.getNotificationsConfiguration()
            .register(CONFIGURE_NOTIFICATION_GROUP_ID, NotificationDisplayType.STICKY_BALLOON, true)

        val connection = project.messageBus.connect()
        connection.subscribe(ProjectTopics.PROJECT_ROOTS, moduleRootListener)

        connection.subscribe(ProjectDataImportListener.TOPIC, ProjectDataImportListener {
            notifyOutdatedBundledCompilerIfNecessary(project)
        })

        notifyKotlinStyleUpdateIfNeeded(project)
    }

    override fun projectOpened() {
        super.projectOpened()

        StartupManager.getInstance(project).registerPostStartupActivity {
            performProjectPostOpenActions()
        }
    }

    fun performProjectPostOpenActions() {
        ApplicationManager.getApplication().executeOnPooledThread {
            DumbService.getInstance(project).waitForSmartMode()

            for (module in getModulesWithKotlinFiles(project)) {
                module.getAndCacheLanguageLevelByDependencies()
            }

            if (!isSyncing) {
                val libraries = findOutdatedKotlinLibraries(project)
                val excludeModules = collectModulesWithOutdatedRuntime(libraries)
                showConfigureKotlinNotificationIfNeeded(project, excludeModules)
            } else {
                notificationPostponed = true
            }
        }
    }

    val isSyncing: Boolean get() = syncDepth.get() > 0

    fun syncStarted() {
        syncDepth.incrementAndGet()
    }

    fun syncDone() {
        syncDepth.decrementAndGet()
    }

    companion object {
        const val CONFIGURE_NOTIFICATION_GROUP_ID = "Configure Kotlin in Project"

        fun getInstanceIfNotDisposed(project: Project): KotlinConfigurationCheckerComponent? {
            return runReadAction {
                if (!project.isDisposed) {
                    project.getComponent(KotlinConfigurationCheckerComponent::class.java)
                        ?: error("Can't find ${KotlinConfigurationCheckerComponent::class} component")
                } else {
                    null
                }
            }
        }
    }
}
