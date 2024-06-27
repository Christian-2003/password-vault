package de.passwordvault.model.storage.settings.items;

import androidx.annotation.NonNull;
import java.util.Set;
import de.passwordvault.model.storage.settings.ConfigSettingContract;


/**
 * Class implements a settings item that can store a string set.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class StringSetItem extends GenericItem<Set<String>> {

    /**
     * Constructor instantiates a new item with the passed config, key and default value.
     *
     * @param config    Config through which to access the preferences and editor.
     * @param key       Key with which to store the setting within the preferences.
     * @param defValue  Default value for the setting.
     */
    public StringSetItem(@NonNull ConfigSettingContract config, @NonNull String key, @NonNull Set<String> defValue) {
        super(config, key, defValue);
    }


    /**
     * Method returns the setting.
     *
     * @return  Setting.
     */
    public Set<String> get() {
        return config.getPreferences().getStringSet(key, defValue);
    }

    /**
     * Method changes the setting to the passed argument.
     *
     * @param value New value for the setting.
     */
    public void set(Set<String> value) {
        config.getEditor().putStringSet(key, value).apply();
    }

}
