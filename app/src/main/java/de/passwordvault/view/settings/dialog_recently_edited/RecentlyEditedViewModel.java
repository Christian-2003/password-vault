package de.passwordvault.view.settings.dialog_recently_edited;

import android.content.Context;
import androidx.lifecycle.ViewModel;
import de.passwordvault.R;
import de.passwordvault.model.storage.settings.Config;


/**
 * Class implements the view model for the {@link RecentlyEditedDialog}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class RecentlyEditedViewModel extends ViewModel {

    /**
     * Field stores all options that can be selected by the user.
     */
    public static final int[] OPTIONS = { 3, 5, 7, 10, 15 };


    /**
     * Method returns the text displaying the currently selected item.
     *
     * @param context   Context from which to load string-resources.
     * @return          Text for the selected option.
     */
    public String getSelectedOption(Context context) {
        return context.getString(R.string.settings_customization_home_recentlyedited_number_option).replace("{arg}", "" + Config.getInstance().numRecentlyEdited.get());
    }


    /**
     * Method changes the number of recently edited entries based on the passed position within the
     * {@link #OPTIONS}-array.
     *
     * @param position  Position within the array to which to set the number of recently edited entries.
     */
    public void setSelectedOption(int position) {
        Config.getInstance().numRecentlyEdited.set(OPTIONS[position]);
    }


    /**
     * Method returns an array containing all options to display to the user.
     *
     * @param context   Context from which to load string-resources.
     * @return          Array of strings containing all options.
     */
    public String[] getOptions(Context context) {
        String[] options = new String[OPTIONS.length];
        String placeholderText = context.getString(R.string.settings_customization_home_recentlyedited_number_option);
        for (int i = 0; i < options.length; i++) {
            options[i] = placeholderText.replace("{arg}", "" + OPTIONS[i]);
        }
        return options;
    }

}
