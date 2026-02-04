package de.christian2003.core.security.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import de.christian2003.core.security.domain.services.BiometricsService
import de.christian2003.core.security.infrastructure.services.AndroidBiometricsService


/**
 * Setup Hilt DI for the domain layer.
 */
@Module
@InstallIn(ActivityComponent::class)
internal abstract class SecurityBindingsActivityModule {

    @Binds
    abstract fun bindBiometricsService(
        impl: AndroidBiometricsService
    ): BiometricsService

}
