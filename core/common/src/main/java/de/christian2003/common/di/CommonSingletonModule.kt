package de.christian2003.common.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


/**
 * DI setup for the :core:common module.
 */
@Module
@InstallIn(SingletonComponent::class)
internal class CommonSingletonModule {

}
