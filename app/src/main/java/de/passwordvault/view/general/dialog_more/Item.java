package de.passwordvault.view.general.dialog_more;

import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import java.io.Serializable;


/**
 * Class models the superclass for all items that can be shown in the {@link MoreDialog}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public abstract class Item implements Serializable {

    /**
     * Field stores the type for button items.
     */
    public static final int TYPE_BUTTON = 2;

    /**
     * Field stores the type for checkbox items.
     */
    public static final int TYPE_CHECKBOX = 4;


    /**
     * Attribute stores the title of the item.
     */
    @NonNull
    private final String title;

    /**
     * Attribute stores the type of the item.
     */
    private final int type;


    /**
     * Constructor instantiates a new item with the passed title.
     *
     * @param title Title for the item.
     * @param type  Type for the item.
     */
    public Item(@NonNull String title, int type) {
        this.title = title;
        this.type = type;
    }


    /**
     * Method returns the title of the item.
     *
     * @return  Title if the item.
     */
    @NonNull
    public String getTitle() {
        return title;
    }

    /**
     * Method returns the type of the item.
     *
     * @return  Type of the item.
     */
    public int getType() {
        return type;
    }

}
