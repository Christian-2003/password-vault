package de.passwordvault.view.general.dialog_more;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;


/**
 * Class implements the view model for the {@link MoreDialog}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class MoreViewModel extends ViewModel {

    /**
     * Attribute stores the title of the dialog.
     */
    @Nullable
    private String title;

    /**
     * Attribute stores the drawable for the title bar of the dialog.
     */
    @DrawableRes
    private int icon;

    /**
     * Attribute stores the list of items to be displayed by the dialog.
     */
    private ArrayList<MoreDialog.Item> items;


    /**
     * Method returns the title of the dialog.
     *
     * @return  Title of the dialog.
     */
    @NonNull
    public String getTitle() {
        if (title != null) {
            return title;
        }
        return "";
    }


    /**
     * Method returns the ID of the icon for the title bar of the dialog.
     *
     * @return  ID of the drawable icon.
     */
    @DrawableRes
    public int getIcon() {
        return icon;
    }


    /**
     * Method returns the list containing the items to display.
     *
     * @return  List of items to display.
     */
    @NonNull
    public ArrayList<MoreDialog.Item> getItems() {
        if (items != null) {
            return items;
        }
        return new ArrayList<>();
    }


    /**
     * Method processes the arguments passed to the dialog.
     *
     * @param args  Arguments passed to the dialog.
     */
    public void processArguments(@NonNull Bundle args) {
        if (args.containsKey(MoreDialog.ARG_TITLE)) {
            title = args.getString(MoreDialog.ARG_TITLE);
        }

        if (args.containsKey(MoreDialog.ARG_ICON)) {
            icon = args.getInt(MoreDialog.ARG_ICON);
        }

        if (args.containsKey(MoreDialog.ARG_ITEMS)) {
            try {
                items = (ArrayList<MoreDialog.Item>)args.getSerializable(MoreDialog.ARG_ITEMS);
            }
            catch (Exception e) {
                //Ignore...
            }
        }
    }

}
