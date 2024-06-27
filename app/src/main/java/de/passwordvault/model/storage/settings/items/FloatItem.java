package de.passwordvault.model.storage.settings.items;

import androidx.annotation.NonNull;
import de.passwordvault.model.storage.settings.ConfigSettingContract;


/**
 * Class implements a settings item that can store a float value.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class FloatItem extends GenericItem<Float> {

    /**
     * Constructor instantiates a new item with the passed config, key and default value.
     *
     * @param config    Config through which to access the preferences and editor.
     * @param key       Key with which to store the setting within the preferences.
     * @param defValue  Default value for the setting.
     */
    public FloatItem(@NonNull ConfigSettingContract config, @NonNull String key, @NonNull Float defValue) {
        super(config, key, defValue);
    }


    /**
     * Method returns the setting.
     *
     * @return  Setting.
     */
    public Float get() {
        return config.getPreferences().getFloat(key, defValue);
    }

    /**
     * Method changes the setting to the passed argument.
     *
     * @param value New value for the setting.
     */
    public void set(Float value) {
        config.getEditor().putFloat(key, value).apply();
    }

}
