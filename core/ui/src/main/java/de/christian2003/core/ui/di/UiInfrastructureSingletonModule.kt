package de.christian2003.core.ui.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.christian2003.core.ui.model.ColorGenerator


@Module
@InstallIn(SingletonComponent::class)
class UiInfrastructureSingletonModule {

    @Provides
    fun provideColorGenerator(): ColorGenerator {
        return ColorGenerator()
    }

}
