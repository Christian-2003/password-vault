package de.passwordvault.view.settings.dialog_darkmode;

import android.app.UiModeManager;
import android.content.Context;
import androidx.lifecycle.ViewModel;
import de.passwordvault.App;
import de.passwordvault.model.storage.settings.Config;


/**
 * Class implements the view model for the {@link DarkmodeDialog}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class DarkmodeViewModel extends ViewModel {

    /**
     * Field is used to indicate that the app uses dark mode.
     */
    public static final int DARK = Config.Constants.DARKMODE_DARK;

    /**
     * Field is used to indicate that the app uses light mode.
     */
    public static final int LIGHT = Config.Constants.DARKMODE_LIGHT;

    /**
     * Field is used to indicate that the app uses the same UI mode as the OS.
     */
    public static final int SYSTEM = Config.Constants.DARKMODE_SYSTEM;


    /**
     * Method returns whether the app uses dark mode, light mode or the same mode as the OS.
     *
     * @return  Constant indicating which mode the app uses.
     */
    public int getDarkmode() {
        return Config.getInstance().darkmode.get();
    }

    /**
     * Method changes whether the app uses dark mode, light mode of the same mode as the OS.
     * If the passed mode is not identical to the current mode, the new mode will be applied
     * immediately.
     *
     * @param mode  Constant indicating which mode the app should use.
     */
    public void setDarkmode(int mode) {
        boolean applyNeeded = mode != getDarkmode();
        Config.getInstance().darkmode.set(mode);
        if (applyNeeded) {
            Config.Methods.applyDarkmode();
        }
    }


    /**
     * Method returns whether the underlying OS currently uses dark mode (= {@code true}) or light
     * mode (= {@code false}).
     *
     * @return  Whether the underlying OS uses dark mode.
     */
    public boolean isUsingDarkmode() {
        UiModeManager manager = (UiModeManager)App.getContext().getSystemService(Context.UI_MODE_SERVICE);
        return manager.getNightMode() == UiModeManager.MODE_NIGHT_YES;
    }

}
