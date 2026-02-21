package de.christian2003.feature.autofill.infrastructure.repositories

import android.content.Context
import android.content.SharedPreferences
import android.view.autofill.AutofillManager
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.feature.autofill.domain.repositories.AutofillConfigRepository
import javax.inject.Inject


/**
 * Implementation for the repository of the autofill configuration. The implementation uses shared
 * preferences as well as AutofillManager.
 */
internal class AndroidAutofillConfigRepository @Inject constructor(
    @ApplicationContext context: Context
) : AutofillConfigRepository {

    /**
     * Android autofill manager is used to see whether our service is selected by the system.
     */
    private val autofillManager: AutofillManager = context.getSystemService(AutofillManager::class.java) as AutofillManager

    /**
     * Preferences for configuration settings.
     */
    private val preferences: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)


    /**
     * Returns whether the autofill service is enabled by the system.
     */
    override fun isSelectedBySystem(): Boolean {
        return autofillManager.hasEnabledAutofillServices()
    }


    /**
     * Returns whether the autofill service is enabled. Even if the service is selected by the
     * Android system, this can return false if the user has disabled autofill within the app.
     *
     * @return  Whether autofill is enabled.
     */
    override fun isAutofillEnabled(): Boolean {
        return preferences.getBoolean("autofill_enabled", true)
    }


    /**
     * Changes whether the autofill service is enabled. If this is set to false, autofill will no
     * longer autofill data for other apps, even if the service is selected by the Android system.
     *
     * @param isAutofillEnabled Whether autofill is enabled.
     */
    override fun setAutofillEnabled(isAutofillEnabled: Boolean) {
        preferences.edit {
            putBoolean("autofill_enabled", isAutofillEnabled)
        }
    }


    /**
     * Returns whether the geocoder is enabled to parse addresses.
     *
     * @return  Whether the geocoder is enabled.
     */
    override fun isGeocoderEnabled(): Boolean {
        return preferences.getBoolean("autofill_geocoder", true)
    }


    /**
     * Changes whether the geocoder is enabled to parse addresses.
     *
     * @param isGeocoderEnabled Whether the geocoder is enabled.
     */
    override fun setGeocoderEnabled(isGeocoderEnabled: Boolean) {
        preferences.edit {
            putBoolean("autofill_geocoder", isGeocoderEnabled)
        }
    }

}
