package de.christian2003.core.common.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.christian2003.core.common.domain.services.ClipboardService
import de.christian2003.core.common.infrastructure.services.AndroidClipboardService


/**
 * DI setup for the :core:common module.
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class CommonBindingsSingletonModule {

    //========================= DOMAIN REPOSITORIES =========================

    @Binds
    abstract fun bindClipboardService(
        impl: AndroidClipboardService
    ): ClipboardService

}
