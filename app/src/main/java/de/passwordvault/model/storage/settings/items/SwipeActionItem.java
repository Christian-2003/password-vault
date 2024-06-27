package de.passwordvault.model.storage.settings.items;

import androidx.annotation.NonNull;
import de.passwordvault.model.detail.DetailSwipeAction;
import de.passwordvault.model.storage.settings.ConfigSettingContract;


/**
 * Class implements a settings item that can store a swipe action.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class SwipeActionItem extends GenericItem<DetailSwipeAction> {

    /**
     * Constructor instantiates a new item with the passed config, key and default value.
     *
     * @param config    Config through which to access the preferences and editor.
     * @param key       Key with which to store the setting within the preferences.
     * @param defValue  Default value for the setting.
     */
    public SwipeActionItem(@NonNull ConfigSettingContract config, @NonNull String key, @NonNull DetailSwipeAction defValue) {
        super(config, key, defValue);
    }


    /**
     * Method returns the setting.
     *
     * @return  Setting.
     */
    public DetailSwipeAction get() {
        return DetailSwipeAction.fromPreferencesValue(config.getPreferences().getString(key, defValue.getPreferencesValue()));
    }

    /**
     * Method changes the setting to the passed argument.
     *
     * @param value New value for the setting.
     */
    public void set(DetailSwipeAction value) {
        config.getEditor().putString(key, value.getPreferencesValue()).apply();
    }

}
