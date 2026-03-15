package de.christian2003.data.files.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.christian2003.data.files.domain.repositories.InternalFilesystemRepository
import de.christian2003.data.files.domain.services.FileCopyService
import de.christian2003.data.files.infrastructure.repositories.AppInternalFilesystemRepository
import de.christian2003.data.files.infrastructure.services.AndroidFileCopyService


@Module
@InstallIn(SingletonComponent::class)
internal abstract class FilesBindingsSingletonModule {

    // ============== DOMAIN REPOSITORIES ==============

    @Binds
    abstract fun bindInternalFilesystemRepository(
        impl: AppInternalFilesystemRepository
    ): InternalFilesystemRepository



    // ============== DOMAIN SERVICES ==============

    @Binds
    abstract fun bindFileCopyService(
        impl: AndroidFileCopyService
    ): FileCopyService

}
