package de.christian2003.feature.autofill.domain.repositories


/**
 * Repository for the autofill configuration.
 */
internal interface AutofillConfigRepository {

    /**
     * Returns whether the autofill service is enabled by the system.
     */
    fun isSelectedBySystem(): Boolean


    /**
     * Returns whether the autofill service is enabled. Even if the service is selected by the
     * Android system, this can return false if the user has disabled autofill within the app.
     *
     * @return  Whether autofill is enabled.
     */
    fun isAutofillEnabled(): Boolean


    /**
     * Changes whether the autofill service is enabled. If this is set to false, autofill will no
     * longer autofill data for other apps, even if the service is selected by the Android system.
     *
     * @param isAutofillEnabled Whether autofill is enabled.
     */
    fun setAutofillEnabled(isAutofillEnabled: Boolean)


    /**
     * Returns whether the geocoder is enabled to parse addresses.
     *
     * @return  Whether the geocoder is enabled.
     */
    fun isGeocoderEnabled(): Boolean


    /**
     * Changes whether the geocoder is enabled to parse addresses.
     *
     * @param isGeocoderEnabled Whether the geocoder is enabled.
     */
    fun setGeocoderEnabled(isGeocoderEnabled: Boolean)

}
