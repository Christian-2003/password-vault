package de.passwordvault.model.storage.settings;

import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import de.passwordvault.model.storage.settings.items.GenericItem;


/**
 * Interface provides methods required by {@link GenericItem} to access and edit preferences.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public interface ConfigSettingContract {

    /**
     * Method returns the preferences used to store the settings.
     *
     * @return  Preferences instance used to store the settings.
     */
    @NonNull
    SharedPreferences getPreferences();

    /**
     * Method returns the editor used to edit the settings.
     *
     * @return  Editor instance used to edit the settings.
     */
    @NonNull
    SharedPreferences.Editor getEditor();

}
