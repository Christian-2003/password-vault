package de.christian2003.passwordvault.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.christian2003.passwordvault.domain.repositories.OtherSoftwareRepository
import de.christian2003.passwordvault.infrastructure.othersoftware.RestOtherSoftwareRepository


@Module
@InstallIn(SingletonComponent::class)
internal abstract class CommonBindingsSingletonModule {

    //========================= DOMAIN REPOSITORIES =========================

    @Binds
    abstract fun bindOtherSoftwareRepository(
        impl: RestOtherSoftwareRepository
    ): OtherSoftwareRepository

}
