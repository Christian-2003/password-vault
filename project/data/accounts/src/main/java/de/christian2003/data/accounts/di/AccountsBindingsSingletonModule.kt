package de.christian2003.data.accounts.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.christian2003.data.accounts.domain.repositories.AccountRepository
import de.christian2003.data.accounts.domain.repositories.DetailRepository
import de.christian2003.data.accounts.domain.repositories.PackagesRepository
import de.christian2003.data.accounts.domain.repositories.TagRepository
import de.christian2003.data.accounts.domain.repositories.TargetRepository
import de.christian2003.data.accounts.domain.services.PackageFingerprintService
import de.christian2003.data.accounts.infrastructure.db.PasswordVaultRepository
import de.christian2003.data.accounts.infrastructure.packages.AndroidPackageFingerprintService
import de.christian2003.data.accounts.infrastructure.packages.LocalPackagesRepository


@Module
@InstallIn(SingletonComponent::class)
internal abstract class AccountsBindingsSingletonModule {

    //========================= DOMAIN REPOSITORIES =========================

    @Binds
    abstract fun bindPackagesRepository(
        impl: LocalPackagesRepository
    ): PackagesRepository

    @Binds
    abstract fun bindAccountRepository(
        impl: PasswordVaultRepository
    ): AccountRepository

    @Binds
    abstract fun bindDetailRepository(
        impl: PasswordVaultRepository
    ): DetailRepository

    @Binds
    abstract fun bindTagRepository(
        impl: PasswordVaultRepository
    ): TagRepository

    @Binds
    abstract fun bindTargetRepository(
        impl: PasswordVaultRepository
    ): TargetRepository


    //========================= DOMAIN SERVICES =========================

    @Binds
    abstract fun bindPackageFingerprintService(
        impl: AndroidPackageFingerprintService
    ): PackageFingerprintService

}
