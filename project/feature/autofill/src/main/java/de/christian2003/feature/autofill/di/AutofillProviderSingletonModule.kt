package de.christian2003.feature.autofill.di

import android.content.Context
import android.location.Geocoder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.christian2003.feature.autofill.domain.AddressParserService
import de.christian2003.feature.autofill.infrastructure.factories.AddressParserServiceFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal class AutofillProviderSingletonModule {

    @Provides
    @Singleton
    fun provideGeocoder(
        @ApplicationContext context: Context
    ): Geocoder {
        return Geocoder(context)
    }


    @Provides
    fun provideAddressParserService(
        addressParserServiceFactory: AddressParserServiceFactory
    ): AddressParserService {
        return addressParserServiceFactory.create()
    }

}
