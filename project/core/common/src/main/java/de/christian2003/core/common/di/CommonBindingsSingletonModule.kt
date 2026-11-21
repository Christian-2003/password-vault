package de.christian2003.core.common.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.christian2003.core.common.domain.services.ClipboardService
import de.christian2003.core.common.domain.services.ConnectivityCheckerService
import de.christian2003.core.common.domain.services.FilesystemService
import de.christian2003.core.common.domain.services.WebRequestService
import de.christian2003.core.common.infrastructure.services.AndroidClipboardService
import de.christian2003.core.common.infrastructure.services.AndroidConnectivityCheckerService
import de.christian2003.core.common.infrastructure.services.AndroidFilesystemService
import de.christian2003.core.common.infrastructure.services.OkHttpWebRequestService


/**
 * DI setup for the :core:common module.
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class CommonBindingsSingletonModule {

    //========================= DOMAIN SERVICES =========================

    @Binds
    abstract fun bindClipboardService(
        impl: AndroidClipboardService
    ): ClipboardService

    @Binds
    abstract fun bindConnectivityCheckerService(
        impl: AndroidConnectivityCheckerService
    ): ConnectivityCheckerService

    @Binds
    abstract fun bindFilesystemService(
        impl: AndroidFilesystemService
    ): FilesystemService

    @Binds
    abstract fun bindWebRequestService(
        impl: OkHttpWebRequestService
    ): WebRequestService

}
