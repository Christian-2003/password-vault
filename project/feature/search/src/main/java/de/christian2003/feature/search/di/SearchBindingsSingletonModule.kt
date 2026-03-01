package de.christian2003.feature.search.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.data.accounts.domain.entities.Detail
import de.christian2003.feature.search.application.services.QueryAccountEvaluatorService
import de.christian2003.feature.search.application.services.QueryDetailEvaluatorService
import de.christian2003.feature.search.application.services.QueryEvaluatorService
import de.christian2003.feature.search.domain.repositories.SearchConfigRepository
import de.christian2003.feature.search.infrastructure.repositories.AndroidSearchConfigRepository


@Module
@InstallIn(SingletonComponent::class)
internal abstract class SearchBindingsSingletonModule {

    //========================= DOMAIN REPOSITORIES =========================

    @Binds
    abstract fun bindSearchConfigRepository(
        impl: AndroidSearchConfigRepository
    ): SearchConfigRepository


    //========================= APPLICATION SERVICES =========================

    @Binds
    abstract fun bindQueryAccountEvaluatorService(
        impl: QueryAccountEvaluatorService
    ): QueryEvaluatorService<Account>

    @Binds
    abstract fun bindQueryDetailEvaluatorService(
        impl: QueryDetailEvaluatorService
    ): QueryEvaluatorService<Detail>

}
