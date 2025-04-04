package de.passwordvault.model.storage.settings;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import java.lang.reflect.Field;
import de.passwordvault.App;
import de.passwordvault.model.detail.DetailSwipeAction;
import de.passwordvault.model.storage.settings.items.BooleanItem;
import de.passwordvault.model.storage.settings.items.FloatItem;
import de.passwordvault.model.storage.settings.items.GenericItem;
import de.passwordvault.model.storage.settings.items.IntItem;
import de.passwordvault.model.storage.settings.items.LongItem;
import de.passwordvault.model.storage.settings.items.StringItem;
import de.passwordvault.model.storage.settings.items.SwipeActionItem;


/**
 * Class implements the configuration for the app.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class Config implements ConfigSettingContract {

    /**
     * Setting indicates whether to use autofill caching.
     */
    public final BooleanItem useAutofillCaching = new BooleanItem(this, "autofill_caching", false);

    /**
     * Setting indicates whether to use autofill authentication.
     */
    public final BooleanItem useAutofillAuth = new BooleanItem(this, "autofill_auth", true);

    /**
     * Setting indicates what to do on left swipe.
     */
    public final SwipeActionItem leftSwipeAction = new SwipeActionItem(this, "left_swipe", DetailSwipeAction.DELETE);

    /**
     * Setting indicates what to do on right swipe.
     */
    public final SwipeActionItem rightSwipeAction = new SwipeActionItem(this, "right_swipe", DetailSwipeAction.EDIT);

    /**
     * Setting indicates whether to include settings in backups.
     */
    public final BooleanItem backupIncludeSettings = new BooleanItem(this, "backup_include_settings", false);

    /**
     * Setting indicates whether to include quality gates in backups.
     */
    public final BooleanItem backupIncludeQualityGates = new BooleanItem(this, "backup_include_quality_gates", false);

    /**
     * Setting indicates whether to encrypt backups.
     */
    public final BooleanItem backupEncrypted = new BooleanItem(this, "backup_encrypted", false);

    /**
     * Setting indicates whether to prevent screenshots of sensitive data.
     */
    public final BooleanItem preventScreenshots = new BooleanItem(this, "prevent_screenshots", true);

    /**
     * Setting indicates whether to open resources (e.g. help or legal pages) in a browser (true) or
     * within the app (false).
     */
    public final BooleanItem openResourcesInBrowser = new BooleanItem(this, "open_resources_in_browser", false);


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


    /**
     * Method returns the generic item from a specific field from the reflection API.
     * If no field exists, {@code null} is returned. The returned field can be casted
     * into a subclass of {@link GenericItem}.
     *
     * @param field Field for which to return the settings item.
     * @return      Item for the passed field.
     */
    public static GenericItem<?> getItemFromField(Field field) {
        try {
            if (field.getType() == BooleanItem.class) {
                return (BooleanItem)field.get(getInstance());
            }
            else if (field.getType() == FloatItem.class) {
                return (FloatItem)field.get(getInstance());
            }
            else if (field.getType() == IntItem.class) {
                return (IntItem)field.get(getInstance());
            }
            else if (field.getType() == LongItem.class) {
                return (LongItem)field.get(getInstance());
            }
            else if (field.getType() == StringItem.class) {
                return (StringItem)field.get(getInstance());
            }
            else if (field.getType() == SwipeActionItem.class) {
                return (SwipeActionItem)field.get(getInstance());
            }
        }
        catch (IllegalAccessException e) {
            //Ignore...
        }
        return null;
    }


    /**
     * Method changes the value of the passed generic item to the encoded string value.
     *
     * @param item                      Item whose value to change.
     * @param value                     String-representation of the value to which the passed item
     *                                  shall be changed.
     * @throws ClassCastException       The passed GenericItem&lt;?&gt; cannot be cast to concrete
     *                                  instance.
     * @throws IllegalArgumentException The passed value cannot be parsed.
     */
    public static void changeItemValue(GenericItem<?> item, String value) throws ClassCastException, IllegalArgumentException {
        try {
            if (item instanceof BooleanItem) {
                ((BooleanItem)item).set(Boolean.parseBoolean(value));
            }
            else if (item instanceof FloatItem) {
                ((FloatItem)item).set(Float.parseFloat(value));
            }
            else if (item instanceof IntItem) {
                ((IntItem)item).set(Integer.parseInt(value));
            }
            else if (item instanceof LongItem) {
                ((LongItem)item).set(Long.parseLong(value));
            }
            else if (item instanceof StringItem) {
                ((StringItem)item).set(value);
            }
            else if (item instanceof SwipeActionItem) {
                DetailSwipeAction action = DetailSwipeAction.fromPreferencesValue(value);
                ((SwipeActionItem)item).set(action);
            }
            else {
                throw new ClassCastException("Cannot cast abstract GenericItem<?> to concrete instance");
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

}
