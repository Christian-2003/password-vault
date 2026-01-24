package de.christian2003.security.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.christian2003.security.domain.repositories.BiometricsRepository
import de.christian2003.security.domain.repositories.CommitRepository
import de.christian2003.security.domain.repositories.DecryptedKekRepository
import de.christian2003.security.domain.repositories.HardwareBackedKeyRepository
import de.christian2003.security.domain.repositories.MasterKeyRepository
import de.christian2003.security.domain.repositories.MasterPasswordRepository
import de.christian2003.security.domain.repositories.RecoveryCodesRepository
import de.christian2003.security.domain.repositories.UnlockedMasterKeyRepository
import de.christian2003.security.domain.services.CipherService
import de.christian2003.security.domain.services.KdfService
import de.christian2003.security.domain.services.KeyGeneratorService
import de.christian2003.security.infrastructure.repositories.KeyStoreHardwareBackedKeyRepository
import de.christian2003.security.infrastructure.repositories.SharedPreferencesKeyRepository
import de.christian2003.security.infrastructure.repositories.UnlockedMasterKeyRepositoryImpl
import de.christian2003.security.infrastructure.services.AesCipherService
import de.christian2003.security.infrastructure.services.AesKeyGeneratorService
import de.christian2003.security.infrastructure.services.Pbkdf2Service


/**
 * Setup Hilt DI for the domain layer.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityBindingsSingletonModule {

    //========================= DOMAIN REPOSITORIES =========================

    @Binds
    abstract fun bindBiometricsRepository(
        impl: SharedPreferencesKeyRepository
    ): BiometricsRepository

    @Binds
    abstract fun bindCommitRepository(
        impl: SharedPreferencesKeyRepository
    ): CommitRepository

    @Binds
    abstract fun bindDecryptedKekRepository(
        impl: SharedPreferencesKeyRepository
    ): DecryptedKekRepository

    @Binds
    abstract fun bindMasterKeyRepository(
        impl: SharedPreferencesKeyRepository
    ): MasterKeyRepository

    @Binds
    abstract fun bindMasterPasswordRepository(
        impl: SharedPreferencesKeyRepository
    ): MasterPasswordRepository

    @Binds
    abstract fun bindRecoveryCodesRepository(
        impl: SharedPreferencesKeyRepository
    ): RecoveryCodesRepository

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
