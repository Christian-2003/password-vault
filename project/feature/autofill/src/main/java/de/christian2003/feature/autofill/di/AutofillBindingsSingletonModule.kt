package de.christian2003.feature.autofill.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.christian2003.feature.autofill.domain.services.PersonNameParserService
import de.christian2003.feature.autofill.infrastructure.services.HeuristicPersonNameParserService


@Module
@InstallIn(SingletonComponent::class)
internal abstract class AutofillBindingsSingletonModule {

    @Binds
    abstract fun bindPersonNameParserService(
        impl: HeuristicPersonNameParserService
    ): PersonNameParserService

}
