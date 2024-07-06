package de.passwordvault.model.storage.settings.items;

import androidx.annotation.NonNull;
import de.passwordvault.model.storage.settings.ConfigSettingContract;


/**
 * Class implements a settings item that can store a long value.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class LongItem extends GenericItem<Long> {

    /**
     * Constructor instantiates a new item with the passed config, key and default value.
     *
     * @param config    Config through which to access the preferences and editor.
     * @param key       Key with which to store the setting within the preferences.
     * @param defValue  Default value for the setting.
     */
    public LongItem(@NonNull ConfigSettingContract config, @NonNull String key, @NonNull Long defValue) {
        super(config, key, defValue);
    }


    /**
     * Method returns the setting.
     *
     * @return  Setting.
     */
    public Long get() {
        return config.getPreferences().getLong(key, defValue);
    }

    /**
     * Method changes the setting to the passed argument.
     *
     * @param value New value for the setting.
     */
    public void set(Long value) {
        config.getEditor().putLong(key, value).apply();
    }

}
