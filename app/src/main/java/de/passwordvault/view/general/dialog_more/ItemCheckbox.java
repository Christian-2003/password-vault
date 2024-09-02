package de.passwordvault.view.general.dialog_more;

import android.widget.CompoundButton;
import org.jetbrains.annotations.NotNull;


/**
 * Class models a checkbox item for the {@link MoreDialog}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class ItemCheckbox extends Item {

    /**
     * Attribute stores the listener to invoke if the checked state changes.
     */
    @NotNull
    private final CompoundButton.OnCheckedChangeListener checkedChangeListener;

    /**
     * Attribute stores whether the checkbox is checked.
     */
    private boolean checked;


    /**
     * Constructor instantiates a new item checkbox.
     *
     * @param title                 Title for the item.
     * @param checkedChangeListener Listener to invoke when the checked state of the checkbox changes.
     * @param checked               Whether the checkbox is checked.
     */
    public ItemCheckbox(@NotNull String title, @NotNull CompoundButton.OnCheckedChangeListener checkedChangeListener, boolean checked) {
        super(title, TYPE_CHECKBOX);
        this.checkedChangeListener = checkedChangeListener;
        this.checked = checked;
    }


    /**
     * Method returns the listener to invoked when the checkbox checked state changes.
     *
     * @return  Checked change listener to invoke.
     */
    @NotNull
    public CompoundButton.OnCheckedChangeListener getCheckedChangeListener() {
        return checkedChangeListener;
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
