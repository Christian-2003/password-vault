package de.passwordvault.model.storage;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import de.passwordvault.App;
import de.passwordvault.model.detail.DetailSwipeAction;


/**
 * Class models the configuration of the application. The class provides easy and comfortable access
 * to (unencrypted) shared preferences for configuration purposes.
 *
 * @author  Christian-2003
 * @version 3.5.4
 */
public class Configuration {

    /**
     * Field stores a value indicating that the app uses light- or dark mode depending on the system
     * settings.
     */
    public static final int DARKMODE_SYSTEM = 0;

    /**
     * Field stores a value indicating that the app uses light mode.
     */
    public static final int DARKMODE_LIGHT = 1;

    /**
     * Field stores a value indicating that the app uses dark mode.
     */
    public static final int DARKMODE_DARK = 2;

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
     * Field stores the key with which to store the number of the most recently edited entries on the
     * home fragment.
     */
    private static final String KEY_NUM_RECENTLY_EDITED = "num_recently_edited";

    /**
     * Field stores the key with which to store the left swipe action for details.
     */
    private static final String KEY_DETAIL_LEFT_SWIPE_ACTION = "detail_left_swipe";

    /**
     * Field stores the key with which to store the right swipe action for details.
     */
    private static final String KEY_DETAIL_RIGHT_SWIPE_ACTION = "detail_right_swipe";

    /**
     * Field stores the key with which to store whether to include settings with backups.
     */
    private static final String KEY_BACKUP_INCLUDE_SETTINGS = "backup_include_settings";

    /**
     * Field stores the key with which to store whether to include quality gates with backups.
     */
    private static final String KEY_BACKUP_INCLUDE_QUALITY_GATES = "backup_include_quality_gates";

    /**
     * Field stores the key with which to store whether to encrypt backups.
     */
    private static final String KEY_BACKUP_ENCRYPT = "backup_encrypt";

    /**
     * Field stores the key with which to store whether to prevent taking screenshots from sensitive
     * data.
     */
    private static final String KEY_PREVENT_SCREENSHOT = "prevent_screenshot";

    /**
     * Field stores the shared preferences instance from which to retrieve data.
     */
    private static final SharedPreferences preferences = App.getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);


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
     * Method changes the number of most recently edited entries shown on the home fragment.
     *
     * @param numberOfRecentlyEdited    Number of entries shown on the home fragment.
     */
    public static void setNumberOfRecentlyEdited(int numberOfRecentlyEdited) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_NUM_RECENTLY_EDITED, numberOfRecentlyEdited);
        editor.apply();
    }

    /**
     * Method returns the number of most recently edited entries shown on the home fragment.
     *
     * @return  Number of entries shown on the home fragment.
     */
    public static int getNumberOfRecentlyEdited() {
        return preferences.getInt(KEY_NUM_RECENTLY_EDITED, 5);
    }


    /**
     * Method changes the left swipe action for when a detail is swiped.
     *
     * @param swipeAction   New swipe action.
     */
    public static void setDetailLeftSwipeAction(DetailSwipeAction swipeAction) {
        if (swipeAction != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_DETAIL_LEFT_SWIPE_ACTION, swipeAction.getPreferencesValue());
            editor.apply();
        }
    }

    /**
     * Method returns the left swipe action for when a detail is swiped.
     *
     * @return  Swipe action.
     */
    public static DetailSwipeAction getDetailLeftSwipeAction() {
        String swipeAction = preferences.getString(KEY_DETAIL_LEFT_SWIPE_ACTION, DetailSwipeAction.DELETE.getPreferencesValue());
        return DetailSwipeAction.fromPreferencesValue(swipeAction);
    }


    /**
     * Method changes the right swipe action for when a detail is swiped.
     *
     * @param swipeAction   New swipe action.
     */
    public static void setDetailRightSwipeAction(DetailSwipeAction swipeAction) {
        if (swipeAction != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_DETAIL_RIGHT_SWIPE_ACTION, swipeAction.getPreferencesValue());
            editor.apply();
        }
    }

    /**
     * Method returns the right swipe action for when a detail is swiped.
     *
     * @return  Swipe action.
     */
    public static DetailSwipeAction getDetailRightSwipeAction() {
        String swipeAction = preferences.getString(KEY_DETAIL_RIGHT_SWIPE_ACTION, DetailSwipeAction.EDIT.getPreferencesValue());
        return DetailSwipeAction.fromPreferencesValue(swipeAction);
    }


    /**
     * Method changes whether backups shall include settings.
     *
     * @param includeSettings   Whether backups include settings.
     */
    public static void setBackupIncludeSettings(boolean includeSettings) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_BACKUP_INCLUDE_SETTINGS, includeSettings);
        editor.apply();
    }

    /**
     * Method returns whether backups include settings.
     *
     * @return  Whether backups include settings.
     */
    public static boolean getBackupIncludeSettings() {
        return preferences.getBoolean(KEY_BACKUP_INCLUDE_SETTINGS, false);
    }


    /**
     * Method changes whether backups shall include quality gates.
     *
     * @param includeQualityGates   Whether backups include quality gates.
     */
    public static void setBackupIncludeQualityGates(boolean includeQualityGates) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_BACKUP_INCLUDE_QUALITY_GATES, includeQualityGates);
        editor.apply();
    }

    /**
     * Method returns whether backups include quality gates.
     *
     * @return  Whether backups include quality gates.
     */
    public static boolean getBackupIncludeQualityGates() {
        return preferences.getBoolean(KEY_BACKUP_INCLUDE_QUALITY_GATES, false);
    }


    /**
     * Method changes whether backups shall be encrypted.
     *
     * @param encryptBackup Whether backups are encrypted.
     */
    public static void setBackupEncrypted(boolean encryptBackup) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_BACKUP_ENCRYPT, encryptBackup);
        editor.apply();
    }

    /**
     * Method returns whether backups are encrypted.
     *
     * @return  Whether backups are encrypted.
     */
    public static boolean getBackupEncrypted() {
        return preferences.getBoolean(KEY_BACKUP_ENCRYPT, false);
    }


    /**
     * Method changes whether screenshots of sensitive data can be created.
     *
     * @param preventScreenshots    Whether screenshots of sensitive data can be created.
     */
    public static void setPreventScreenshots(boolean preventScreenshots) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_PREVENT_SCREENSHOT, preventScreenshots);
        editor.apply();
    }

    /**
     * Method returns whether screenshots of sensitive data can be created.
     *
     * @return  Whether screenshots of sensitive data can be created.
     */
    public static boolean getPreventScreenshots() {
        return preferences.getBoolean(KEY_PREVENT_SCREENSHOT, true);
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
