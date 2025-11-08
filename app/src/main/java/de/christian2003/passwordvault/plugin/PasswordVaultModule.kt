package de.christian2003.passwordvault.plugin

import android.app.Activity
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.components.SingletonComponent
import de.christian2003.passwordvault.application.repository.AuthRepository
import de.christian2003.passwordvault.application.security.BiometricAuthService
import de.christian2003.passwordvault.plugin.infrastructure.security.auth.AndroidBiometricAuthService
import de.christian2003.passwordvault.plugin.infrastructure.security.auth.SharedPreferencesAuthRepository
import de.christian2003.passwordvault.plugin.presentation.ui.composables.ContextAction


/**
 * Module defines bindings for dependency injections.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SingletonModule {

    @Binds
    abstract fun bindAuthRepository(
        impl: SharedPreferencesAuthRepository
    ): AuthRepository

}

@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityModule {

    @Binds
    abstract fun bindBiometricAuthService(
        impl: AndroidBiometricAuthService
    ): BiometricAuthService

}
