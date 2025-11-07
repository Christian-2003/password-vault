package de.christian2003.passwordvault.plugin

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.christian2003.passwordvault.application.repository.AuthRepository
import de.christian2003.passwordvault.plugin.infrastructure.security.auth.SharedPreferencesAuthRepository


/**
 * Module defines bindings for dependency injections.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class PasswordVaultModule {

    @Binds
    abstract fun bindAuthRepository(
        impl: SharedPreferencesAuthRepository
    ): AuthRepository

}
