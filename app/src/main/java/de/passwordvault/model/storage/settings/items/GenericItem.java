package de.passwordvault.model.storage.settings.items;

import androidx.annotation.NonNull;
import de.passwordvault.model.storage.settings.ConfigSettingContract;


/**
 * Class implements a generic setting. Extend this class and implement the methods {@link #get()}
 * and {@link #set(T)} to access specific types of settings.
 *
 * @param <T>   Generic type for the setting.
 * @author      Christian-2003
 * @version     3.6.0
 */
public abstract class GenericItem<T> {

    /**
     * Attribute stores the config through which to access the preferences and editor.
     */
    protected ConfigSettingContract config;

    /**
     * Attribute stores the key with which to store the item within the settings.
     */
    protected String key;

    /**
     * Attribute stores the default value for the setting.
     */
    protected T defValue;


    /**
     * Constructor instantiates a new generic item with the passed config, key and default value.
     *
     * @param config    Config through which to access the preferences and editor.
     * @param key       Key with which to store the setting within the preferences.
     * @param defValue  Default value for the setting.
     */
    public GenericItem(@NonNull ConfigSettingContract config, @NonNull String key, @NonNull T defValue) {
        this.config = config;
        this.key = key;
        this.defValue = defValue;
    }


    /**
     * Method returns the key of the item.
     *
     * @return  Key with which to store the item.
     */
    public String getKey() {
        return key;
    }


    /**
     * Method returns the setting.
     *
     * @return  Setting.
     */
    public abstract T get();

    /**
     * Method changes the setting to the passed argument.
     *
     * @param value New value for the setting.
     */
    public abstract void set(T value);

}
