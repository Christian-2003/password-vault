package de.passwordvault.view.general.dialog_more;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Class models a checkbox item for the {@link MoreDialog}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class ItemCheckbox extends Item {

    /**
     * Attribute stores whether the checkbox is checked.
     */
    private boolean checked;


    /**
     * Constructor instantiates a new item checkbox.
     *
     * @param title     Title for the item.
     * @param tag       Tag for the item.
     * @param checked   Whether the checkbox is checked.
     */
    public ItemCheckbox(@NotNull String title, @Nullable String tag, boolean checked) {
        super(title, tag, TYPE_CHECKBOX);
        this.checked = checked;
    }


    /**
     * Method returns whether the checkbox is checked.
     *
     * @return  Whether the checkbox is checked.
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * Method changes whether the checkbox is checked.
     *
     * @param checked   Whether the checkbox is checked.
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}
