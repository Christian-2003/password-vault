package de.passwordvault.model.storage;

import android.content.Context;
import android.content.SharedPreferences;
import de.passwordvault.App;


/**
 * Class models the configuration of the application. The class provides easy and comfortable access
 * to (unencrypted) shared preferences for configuration purposes.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class Configuration {

    /**
     * Field stores the key with which to store whether the data has been converted to version 3.3.0.
     */
    private static final String KEY_CONVERTED_STORAGE_1 = "converted_storage_1";

    /**
     * Field stores the key with which to store whether autofill shall use caching.
     */
    private static final String KEY_AUTOFILL_CACHING = "autofill_caching";

    /**
     * Field stores the key with which to store whether autofill shall require authentication.
     */
    private static final String KEY_AUTOFILL_AUTHENTICATION = "autofill_authentication";

    /**
     * Field stores the shared preferences instance from which to retrieve data.
     */
    private static final SharedPreferences preferences = App.getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);


    /**
     * Method changes whether the data has been converted to version 3.3.0.
     *
     * @param converted Whether the data has been converted.
     */
    public static void setConvertedStorage1(boolean converted) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_CONVERTED_STORAGE_1, converted);
        editor.apply();
    }

    /**
     * Method returns whether the stored data has been converted to version 3.3.0.
     *
     * @return  Whether the data has been converted.
     */
    public static boolean getConvertedStorage1() {
        return preferences.getBoolean(KEY_CONVERTED_STORAGE_1, false);
    }

    /**
     * Method changes whether the autofill service shall use caching.
     *
     * @param autofillCaching   Whether the autofill service shall use caching.
     */
    public static void setAutofillCaching(boolean autofillCaching) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_AUTOFILL_CACHING, autofillCaching);
        editor.apply();
    }

    /**
     * Method returns whether the autofill service uses caching.
     *
     * @return  Whether the autofill service uses caching.
     */
    public static boolean useAutofillCaching() {
        return preferences.getBoolean(KEY_AUTOFILL_CACHING, true);
    }

    /**
     * Method changes whether the autofill service shall require authentication.
     *
     * @param autofillAuthentication    Whether the autofill service shall require authentication.
     */
    public static void setAutofillAuthentication(boolean autofillAuthentication) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_AUTOFILL_AUTHENTICATION, autofillAuthentication);
        editor.apply();
    }

    /**
     * Method returns whether the autofill service requires authentication.
     *
     * @return  Whether the autofill service requires authentication.
     */
    public static boolean useAutofillAuthentication() {
        return preferences.getBoolean(KEY_AUTOFILL_AUTHENTICATION, true);
    }

}
