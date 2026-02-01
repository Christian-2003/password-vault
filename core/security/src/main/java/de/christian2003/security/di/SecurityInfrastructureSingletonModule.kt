package de.christian2003.security.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.christian2003.security.application.services.RecoveryCodeEncoderService
import de.christian2003.security.application.services.SaltGeneratorService


/**
 * Setup Hilt DI for the infrastructure layer.
 */
@Module
@InstallIn(SingletonComponent::class)
internal class SecurityInfrastructureSingletonModule {

    @Provides
    fun provideRecoveryCodeEncoderService(): RecoveryCodeEncoderService {
        return RecoveryCodeEncoderService()
    }


    @Provides
    fun provideSaltGeneratorService(): SaltGeneratorService {
        return SaltGeneratorService()
    }

}
