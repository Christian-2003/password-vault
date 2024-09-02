package de.passwordvault.view.general.dialog_more;

import android.view.View;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;


/**
 * Class models an item for the {@link MoreDialog} that can be used as button.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class ItemButton extends Item {

    /**
     * Attribute stores the icon of the item.
     */
    @DrawableRes
    private final int icon;

    /**
     * Attribute stores the click listener to invoke when the item is clicked.
     */
    @NonNull
    private final View.OnClickListener clickListener;


    /**
     * Constructor instantiates a new item with the passed arguments.
     *
     * @param title         Title for the item.
     * @param icon          Icon for the item.
     * @param clickListener Click listener to invoke when the item is clicked.
     */
    public ItemButton(@NonNull String title, @DrawableRes int icon, @NonNull View.OnClickListener clickListener) {
        super(title, TYPE_BUTTON);
        this.icon = icon;
        this.clickListener = clickListener;
    }


    /**
     * Method returns the icon of the item.
     *
     * @return  Icon of the item.
     */
    @DrawableRes
    public int getIcon() {
        return icon;
    }

    /**
     * Method returns the click listener to invoke when the item is clicked.
     *
     * @return  Click listener to invoke when the item is clicked.
     */
    @NonNull
    public View.OnClickListener getClickListener() {
        return clickListener;
    }

}
