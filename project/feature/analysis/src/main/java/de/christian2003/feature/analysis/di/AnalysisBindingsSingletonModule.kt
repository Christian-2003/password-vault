package de.christian2003.feature.analysis.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.christian2003.feature.analysis.domain.repositories.LookupRepository
import de.christian2003.feature.analysis.infrastructure.lookup.AssetLookupRepository


@Module
@InstallIn(SingletonComponent::class)
internal abstract class AnalysisBindingsSingletonModule {

    //========================= DOMAIN REPOSITORIES =========================

    @Binds
    abstract fun bindLookupRepository(
        impl: AssetLookupRepository
    ): LookupRepository

}
