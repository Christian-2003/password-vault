package de.passwordvault.model.detail;

import de.passwordvault.App;
import de.passwordvault.R;


/**
 * Enum contains fields to specify the type of a {@link Detail}-instance. Each type contains a
 * {@link #displayName}, which stores the display name for the detail (This display name is loaded
 * from the resources of the application and might therefore be different for different locales).
 *
 * @author  Christian-2003
 * @version 3.0.0
 */
public enum DetailType {

    /**
     * Field for text details.
     */
    TEXT(R.string.detail_text, (byte)0),

    /**
     * Field for number details.
     */
    NUMBER(R.string.detail_number, (byte)1),

    /**
     * Field for security question details.
     */
    SECURITY_QUESTION(R.string.detail_security_question, (byte)2),

    /**
     * Field for address details.
     */
    ADDRESS(R.string.detail_address, (byte)3),

    /**
     * Field for date details.
     */
    DATE(R.string.detail_date, (byte)4),

    /**
     * Field for email details.
     */
    EMAIL(R.string.detail_email, (byte)5),

    /**
     * Field for password details.
     */
    PASSWORD(R.string.detail_password, (byte)6),

    /**
     * Field for URL details.
     */
    URL(R.string.detail_url, (byte)7),

    /**
     * Field for undefined details.
     * This field must always be last, so that the ordinal of each {@link DetailType} matches a
     * corresponding index in the array generated by {@link Detail#getTypes()}.
     */
    UNDEFINED(-1, (byte)-1);


    /**
     * Attribute stores the resource string of the display name.
     */
    private final String displayName;

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
     * @param persistentId  Id for persistent storage of the type.
     */
    DetailType(int displayNameId, byte persistentId) {
        if (displayNameId >= 0) {
            displayName = App.getContext().getString(displayNameId);
        }
        else {
            displayName = "";
        }
        this.persistentId = persistentId;
    }


    /**
     * Method returns the display name of the field. The display name is taken from the resources,
     * therefore this might be different for different locales.
     *
     * @return  Display name of the field.
     */
    public String getDisplayName() {
        return displayName;
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
