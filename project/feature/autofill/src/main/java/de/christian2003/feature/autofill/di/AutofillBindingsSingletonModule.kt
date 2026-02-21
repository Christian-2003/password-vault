package de.christian2003.feature.autofill.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.christian2003.feature.autofill.domain.repositories.AutofillConfigRepository
import de.christian2003.feature.autofill.domain.services.DateParserService
import de.christian2003.feature.autofill.domain.services.PersonNameParserService
import de.christian2003.feature.autofill.domain.services.PhoneNumberParserService
import de.christian2003.feature.autofill.infrastructure.repositories.AndroidAutofillConfigRepository
import de.christian2003.feature.autofill.infrastructure.services.HeuristicDateParserService
import de.christian2003.feature.autofill.infrastructure.services.HeuristicPersonNameParserService
import de.christian2003.feature.autofill.infrastructure.services.LibPhoneNumberParserService


@Module
@InstallIn(SingletonComponent::class)
internal abstract class AutofillBindingsSingletonModule {

    //========================= DOMAIN REPOSITORIES =========================

    @Binds
    abstract fun bindAutofillConfigRepository(
        impl: AndroidAutofillConfigRepository
    ): AutofillConfigRepository


    //========================= DOMAIN SERVICES =========================

    @Binds
    abstract fun bindDateParserService(
        impl: HeuristicDateParserService
    ): DateParserService

    @Binds
    abstract fun bindPersonNameParserService(
        impl: HeuristicPersonNameParserService
    ): PersonNameParserService

    @Binds
    abstract fun bindPhoneNumberParserService(
        impl: LibPhoneNumberParserService
    ): PhoneNumberParserService

}
