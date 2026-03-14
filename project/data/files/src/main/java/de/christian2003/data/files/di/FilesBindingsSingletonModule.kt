package de.christian2003.data.files.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.christian2003.data.files.domain.repositories.InternalFilesystemRepository
import de.christian2003.data.files.infrastructure.repositories.AppInternalFilesystemRepository


@Module
@InstallIn(SingletonComponent::class)
internal abstract class FilesBindingsSingletonModule {

    @Binds
    abstract fun bindInternalFilesystemRepository(
        impl: AppInternalFilesystemRepository
    ): InternalFilesystemRepository

}
