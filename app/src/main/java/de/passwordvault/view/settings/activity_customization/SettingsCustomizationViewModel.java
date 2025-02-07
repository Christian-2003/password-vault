package de.passwordvault.view.settings.activity_customization;

import androidx.lifecycle.ViewModel;
import de.passwordvault.model.storage.settings.Config;


/**
 * Class implements the view model for {@link SettingsCustomizationActivity}.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class SettingsCustomizationViewModel extends ViewModel {

    /**
     * Method returns whether to open resources in the browser.
     *
     * @return  Whether to open resources in the browser.
     */
    public boolean openResourcesInBrowser() {
        return Config.getInstance().openResourcesInBrowser.get();
    }


    /**
     * Method changes whether to open resources in the browser.
     *
     * @param openResourcesInBrowser    Whether to open resources in the browser.
     */
    public void openResourcesInBrowser(boolean openResourcesInBrowser) {
        Config.getInstance().openResourcesInBrowser.set(openResourcesInBrowser);
    }

}
