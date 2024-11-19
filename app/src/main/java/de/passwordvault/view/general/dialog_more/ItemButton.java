package de.passwordvault.view.general.dialog_more;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


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
     * Constructor instantiates a new item with the passed arguments.
     *
     * @param title Title for the item.
     * @param tag   Tag for the item.
     * @param icon  Icon for the item.
     */
    public ItemButton(@NonNull String title, @Nullable String tag, @DrawableRes int icon) {
        super(title, tag, TYPE_BUTTON);
        this.icon = icon;
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

}
