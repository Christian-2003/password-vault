package de.christian2003.security.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.christian2003.security.domain.repositories.AuthTransactionRepository
import de.christian2003.security.domain.repositories.HardwareBackedKeyRepository
import de.christian2003.security.domain.repositories.ReadonlyAuthRepository
import de.christian2003.security.domain.repositories.UnlockedMasterKeyRepository
import de.christian2003.security.domain.services.CipherService
import de.christian2003.security.domain.services.KdfService
import de.christian2003.security.domain.services.KeyGeneratorService
import de.christian2003.security.infrastructure.repositories.KeyStoreHardwareBackedKeyRepository
import de.christian2003.security.infrastructure.repositories.SharedPreferencesAuthRepository
import de.christian2003.security.infrastructure.repositories.UnlockedMasterKeyRepositoryImpl
import de.christian2003.security.infrastructure.services.AesCipherService
import de.christian2003.security.infrastructure.services.AesKeyGeneratorService
import de.christian2003.security.infrastructure.services.Pbkdf2Service


/**
 * Setup Hilt DI for the domain layer.
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class SecurityBindingsSingletonModule {

    //========================= DOMAIN REPOSITORIES =========================

    @Binds
    abstract fun bindAuthTransactionRepository(
        impl: SharedPreferencesAuthRepository
    ): AuthTransactionRepository

    @Binds
    abstract fun bindReadonlyAuthRepository(
        impl: SharedPreferencesAuthRepository
    ): ReadonlyAuthRepository

    @Binds
    abstract fun bindHardwareBackedKeyRepository(
        impl: KeyStoreHardwareBackedKeyRepository
    ): HardwareBackedKeyRepository

    @Binds
    abstract fun bindUnlockedMasterKeyRepository(
        impl: UnlockedMasterKeyRepositoryImpl
    ): UnlockedMasterKeyRepository


    //========================= DOMAIN SERVICES =========================

    @Binds
    abstract fun bindCipherService(
        impl: AesCipherService
    ): CipherService

    @Binds
    abstract fun bindKdfService(
        impl: Pbkdf2Service
    ): KdfService

    @Binds
    abstract fun bindKeyGeneratorService(
        impl: AesKeyGeneratorService
    ): KeyGeneratorService

}
