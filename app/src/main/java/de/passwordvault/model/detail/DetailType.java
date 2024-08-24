package de.passwordvault.model.detail;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import de.passwordvault.App;
import de.passwordvault.R;


/**
 * Enum contains fields to specify the type of a {@link Detail}-instance. Each type contains a
 * {@link #getDisplayName()}, which stores the display name for the detail (This display name is loaded
 * from the resources of the application and might therefore be different for different locales).
 *
 * @author  Christian-2003
 * @version 3.2.1
 */
public enum DetailType {

    /**
     * Field for text details.
     */
    TEXT(R.string.detail_text, R.drawable.detail_text, (byte)0),

    /**
     * Field for number details.
     */
    NUMBER(R.string.detail_number, R.drawable.detail_number, (byte)1),

    /**
     * Field for security question details.
     */
    SECURITY_QUESTION(R.string.detail_security_question, R.drawable.detail_security_question, (byte)2),

    /**
     * Field for address details.
     */
    ADDRESS(R.string.detail_address, R.drawable.detail_address, (byte)3),

    /**
     * Field for date details.
     */
    DATE(R.string.detail_date, R.drawable.detail_date, (byte)4),

    /**
     * Field for email details.
     */
    EMAIL(R.string.detail_email, R.drawable.detail_email, (byte)5),

    /**
     * Field for password details.
     */
    PASSWORD(R.string.detail_password, R.drawable.detail_password, (byte)6),

    /**
     * Field for URL details.
     */
    URL(R.string.detail_url, R.drawable.detail_url, (byte)7),

    /**
     * Field for PIN code details.
     */
    PIN(R.string.detail_pin, R.drawable.detail_pin, (byte)8),

    /**
     * Field for undefined details.
     * This field must always be last, so that the ordinal of each {@link DetailType} matches a
     * corresponding index in the array generated by {@link Detail#getTypes()}.
     */
    UNDEFINED(-1, R.drawable.detail_text, (byte)-1);


    /**
     * Attribute stores the resource string of the display name.
     */
    @StringRes
    private final int displayNameId;

    /**
     * Attribute stores the ID of the drawable resource associated with the detail type.
     */
    @DrawableRes
    private final int drawable;

    /**
     * Attribute stores an id for the detail type. This is needed for when a {@link Detail} is saved
     * to secondary storage. In case there is an app update which adds new detail types, the app needs
     * a way to remember which type was stored on secondary storage for each detail.
     */
    private final byte persistentId;


    /**
     * Constructor instantiates a new {@link DetailType}-field with the passed display name. Pass a
     * negative resource id in order to not provide any display name.
     *
     * @param displayNameId Resource id of the display name for the field.
     * @param drawable      ID of the drawable resource associated with the detail.
     * @param persistentId  Id for persistent storage of the type.
     */
    DetailType(@StringRes int displayNameId, @DrawableRes int drawable, byte persistentId) {
        this.displayNameId = displayNameId;
        this.drawable = drawable;
        this.persistentId = persistentId;
    }


    /**
     * Method returns the display name of the field. The display name is taken from the resources,
     * therefore this might be different for different locales.
     *
     * @return  Display name of the field.
     */
    public String getDisplayName() {
        if (displayNameId != -1) {
            return App.getContext().getString(displayNameId);
        }
        return "";
    }

    /**
     * Attribute returns the ID of the drawable resource associated with the type.
     *
     * @return  Drawable resource ID.
     */
    @DrawableRes
    public int getDrawable() {
        return drawable;
    }

    /**
     * Method returns the placeholder for the name of a detail. If no suitable name placeholder exists,
     * null is returned.
     *
     * @return  Suitable name placeholder or {@code null}.
     */
    public String getNamePlaceholder() {
        switch (this) {
            case EMAIL:
            case PASSWORD:
            case PIN:
                return App.getContext().getString(displayNameId);
        }
        return null;
    }

    /**
     * Method returns whether it is advisable to obfuscate the DetailType.
     *
     * @return  Whether it is advisable to obfuscate the type.
     */
    public boolean shouldObfuscate() {
        switch (this) {
            case PASSWORD:
            case PIN:
                return true;
        }
        return false;
    }

    /**
     * Method returns the persistent id of the field.
     *
     * @return  Persistent id of the field.
     */
    public byte getPersistentId() {
        return persistentId;
    }

}
