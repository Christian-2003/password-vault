package de.passwordvault.model.storage.settings;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import de.passwordvault.App;
import de.passwordvault.model.detail.DetailSwipeAction;
import de.passwordvault.model.storage.settings.items.BooleanItem;
import de.passwordvault.model.storage.settings.items.IntItem;
import de.passwordvault.model.storage.settings.items.SwipeActionItem;


/**
 * Class implements the configuration for the app.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class Config implements ConfigSettingContract {

    /**
     * Setting indicates whether to use autofill caching.
     */
    public BooleanItem useAutofillCaching = new BooleanItem(this, "autofill_caching", false);

    /**
     * Setting indicates whether to use autofill authentication.
     */
    public BooleanItem useAutofillAuth = new BooleanItem(this, "autofill_auth", true);

    /**
     * Setting indicate whether to use the darkmode.
     */
    public IntItem darkmode = new IntItem(this, "darkmode", 0); //TODO: Replace '0' with constant.

    /**
     * Setting indicates how many recently edited entries to show on home screen.
     */
    public IntItem numRecentlyEdited = new IntItem(this, "num_recently_edited", 5);

    /**
     * Setting indicates what to do on left swipe.
     */
    public SwipeActionItem leftSwipeAction = new SwipeActionItem(this, "left_swipe", DetailSwipeAction.DELETE);

    /**
     * Setting indicates what to do on right swipe.
     */
    public SwipeActionItem rightSwipeAction = new SwipeActionItem(this, "right_swipe", DetailSwipeAction.EDIT);

    /**
     * Setting indicates whether to include settings in backups.
     */
    public BooleanItem backupIncludeSettings = new BooleanItem(this, "backup_include_settings", false);

    /**
     * Setting indicates whether to include quality gates in backups.
     */
    public BooleanItem backupIncludeQualityGates = new BooleanItem(this, "backup_include_quality_gates", false);

    /**
     * Setting indicates whether to encrypt backups.
     */
    public BooleanItem backupEncrypted = new BooleanItem(this, "backup_encrypted", false);

    /**
     * Setting indicates whether to prevent screenshots of sensitive data.
     */
    @NoBackup
    public BooleanItem preventScreenshots = new BooleanItem(this, "prevent_screenshots", false);


    /**
     * Attribute stores the singleton instance for the config.
     */
    private static Config singleton;


    /**
     * Attribute stores the preferences used to store the settings.
     */
    private final SharedPreferences preferences;

    /**
     * Attribute stores the editor used to editor the setting.
     */
    private final SharedPreferences.Editor editor;


    /**
     * Constructor instantiates a new config.
     */
    private Config() {
        preferences = App.getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }


    /**
     * Method returns the preferences used to store the settings.
     *
     * @return  Preferences instance used to store the settings.
     */
    @NonNull
    @Override
    public SharedPreferences getPreferences() {
        return preferences;
    }

    /**
     * Method returns the editor used to edit the settings.
     *
     * @return  Editor instance used to edit the settings.
     */
    @NonNull
    @Override
    public SharedPreferences.Editor getEditor() {
        return editor;
    }


    /**
     * Method returns the singleton instance for the config.
     *
     * @return  Singleton instance for the config.
     */
    public static Config getInstance() {
        if (singleton == null) {
            singleton = new Config();
        }
        return singleton;
    }

}
