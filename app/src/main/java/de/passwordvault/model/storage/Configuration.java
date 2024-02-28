package de.passwordvault.model.storage;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import de.passwordvault.App;
import de.passwordvault.model.storage.csv.CsvBuilder;
import de.passwordvault.model.storage.csv.CsvParser;


/**
 * Class models the configuration of the application. The class provides easy and comfortable access
 * to (unencrypted) shared preferences for configuration purposes.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class Configuration {

    /**
     * Field stores the key with which to store whether the data has been converted to version 3.3.0.
     */
    private static final String KEY_CONVERTED_STORAGE_1 = "converted_storage_1";

    /**
     * Field stores the shared preferences instance from which to retrieve data.
     */
    private static final SharedPreferences preferences = App.getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);


    /**
     * Method changes whether the data has been converted to version 3.3.0.
     *
     * @param converted Whether the data has been converted.
     */
    public static void setConvertedStorage1(boolean converted) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_CONVERTED_STORAGE_1, converted);
        editor.apply();
    }

    /**
     * Method returns whether the stored data has been converted to version 3.3.0.
     *
     * @return  Whether the data has been converted.
     */
    public static boolean getConvertedStorage1() {
        return preferences.getBoolean(KEY_CONVERTED_STORAGE_1, false);
    }

}
