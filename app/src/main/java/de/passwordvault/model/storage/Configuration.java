package de.passwordvault.model.storage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import de.passwordvault.App;


/**
 * Class models the configuration of the application. The class provides easy and comfortable access
 * to (unencrypted) shared preferences for configuration purposes.
 *
 * @author  Christian-2003
 * @version 3.5.1
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
     * Field stores the key with which to store whether to use dark- / light-mode.
     */
    private static final String KEY_APPEARANCE_DARKMODE = "darkmode";

    /**
     * Field stores the shared preferences instance from which to retrieve data.
     */
    private static final SharedPreferences preferences = App.getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);


    /**
     * Field stores a value indicating that the app uses light mode.
     */
    public static final int DARKMODE_LIGHT = 0;

    /**
     * Field stores a value indicating that the app uses dark mode.
     */
    public static final int DARKMODE_DARK = 1;

    /**
     * Field stores a value indicating that the app uses light- or dark mode depending on the system
     * settings.
     */
    public static final int DARKMODE_SYSTEM = 2;


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


    /**
     * Method changes whether the app uses dark mode or light mode.
     *
     * @param setting   Whether the app uses dark or light mode.
     */
    public static void setDarkmode(int setting) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_APPEARANCE_DARKMODE, setting);
        editor.apply();
    }

    /**
     * Method returns whether the app uses dark mode or light mode.
     *
     * @return  Whether the app uses dark or light mode.
     */
    public static int getDarkmode() {
        return preferences.getInt(KEY_APPEARANCE_DARKMODE, DARKMODE_SYSTEM);
    }


    /**
     * Method applies the darkmode setting to the app. This needs to be called whenever the darkmode
     * changes and when the app starts.
     */
    public static void applyDarkmode() {
        int darkmode = getDarkmode();
        switch (darkmode) {
            case DARKMODE_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case DARKMODE_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

}
