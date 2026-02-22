package de.christian2003.feature.search.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.christian2003.feature.search.domain.repositories.SearchConfigRepository
import de.christian2003.feature.search.infrastructure.repositories.AndroidSearchConfigRepository


@Module
@InstallIn(SingletonComponent::class)
internal abstract class SearchBindingsSingletonModule {

    @Binds
    abstract fun bindSearchConfigRepository(
        impl: AndroidSearchConfigRepository
    ): SearchConfigRepository

}
