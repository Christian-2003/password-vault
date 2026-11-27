package de.christian2003.feature.analysis.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import de.christian2003.core.common.domain.abstraction.LandingPlugin
import de.christian2003.feature.analysis.domain.repositories.LookupRepository
import de.christian2003.feature.analysis.infrastructure.lookup.OnlineLookupRepository
import de.christian2003.feature.analysis.presentation.ui.summary.SummaryPlugin


@Module
@InstallIn(SingletonComponent::class)
internal abstract class AnalysisBindingsSingletonModule {

    //========================= DOMAIN REPOSITORIES =========================

    @Binds
    abstract fun bindLookupRepository(
        impl: OnlineLookupRepository
    ): LookupRepository



    //========================= PLUGINS =========================

    @Binds
    @IntoSet
    abstract fun bindSummaryPlugin(
        impl: SummaryPlugin
    ): LandingPlugin

}
