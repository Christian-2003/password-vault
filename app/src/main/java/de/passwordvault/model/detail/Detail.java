package de.passwordvault.model.detail;

import androidx.annotation.NonNull;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;


/**
 * Class models a detail which can contain all types of detailed information for an entry.
 *
 * @author  Christian-2003
 * @version 3.0.0
 */
public class Detail {

    /**
     * Attribute stores type 4 UUID of the detail.
     */
    private String uuid;

    /**
     * Attribute stores name of the detail.
     */
    private String name;

    /**
     * Attribute stores content of the detail.
     */
    private String content;

    /**
     * Attribute stores the date on which the detail was created.
     */
    private Calendar created;

    /**
     * Attribute stores the date on which the detail was changed the last time.
     */
    private Calendar changed;

    /**
     * Attribute stores the type of the detail.
     * The type defines the format of the content. This is used to enhance the way that the detail
     * is later displayed within the frontend.
     */
    private DetailType type;

    /**
     * Attribute stores whether the detail shall be visible by default.
     */
    private boolean visible;

    /**
     * Attribute stores whether the details content shall be obfuscated when displayed. This might
     * be used to display passwords as '******' instead of 'abc123'.
     */
    private boolean obfuscated;

    /**
     * Attribute stores whether the detail shall be encrypted when stored on persistent memory.
     */
    private boolean encrypted;


    /**
     * Constructor instantiates a new Detail with a random type 4 UUID, and no contents.
     */
    public Detail() {
        uuid = UUID.randomUUID().toString();
        name = "";
        content = "";
        created = Calendar.getInstance();
        changed = created;
        type = DetailType.UNDEFINED;
        visible = true;
        obfuscated = false;
        encrypted = false;
    }

    /**
     * Constructor instantiates a new Detail and copies the attributes of the passed Detail to this
     * instance.
     *
     * @param detail    Detail whose values shall be copied to this instance.
     */
    public Detail(Detail detail) {
        if (detail == null) {
            throw new NullPointerException();
        }
        copyAttributesFromDetail(detail);
    }

    /**
     * Constructor instantiates a new Detail with the specified arguments.
     *
     * @param uuid      Type 4 UUID for the detail.
     * @param name      Name for the detail.
     * @param content   Content of the detail.
     * @param created   Date on which the detail was created.
     * @param changed   Date on which the detail was changed the last time.
     * @param type      Type for the detail.
     */
    public Detail(String uuid, String name, String content, Calendar created, Calendar changed, DetailType type) {
        this.uuid = uuid;
        this.name = name;
        this.content = content;
        this.created = created;
        this.changed = changed;
        this.type = type;
        visible = true;
        obfuscated = false;
        encrypted = false;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public Calendar getChanged() {
        return changed;
    }

    public void setChanged(Calendar changed) {
        this.changed = changed;
    }

    public DetailType getType() {
        return type;
    }

    public void setType(DetailType type) {
        this.type = type;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isObfuscated() {
        return obfuscated;
    }

    public void setObfuscated(boolean obfuscated) {
        this.obfuscated = obfuscated;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }


    /**
     * Method tests whether the UUID of the passed detail is identical to the UUID of this detail.
     *
     * @param obj   Detail whose UUID shall be compared to the UUID of this detail.
     * @return      Whether both UUIDs are identical.
     */
    public boolean equals(Object obj) {
        if (obj instanceof Detail) {
            Detail detail = (Detail)obj;
            if (detail.getUuid().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generates a hash for this detail based on the UUID.
     *
     * @return  Generated hash for the detail.
     */
    public int hashCode() {
        return Objects.hash(uuid);
    }


    /**
     * Notifies this Detail that some of its data was changed. This will update the value of
     * {@linkplain #changed} to the current date and time.
     */
    public void notifyDataChange() {
        changed = Calendar.getInstance();
    }


    /**
     * Static method returns a {@linkplain String} array which contains a String representation
     * for the available types.
     *
     * @return          String-array containing the String representations for the Detail types.
     */
    public static String[] GET_TYPES() {
        DetailType[] types = DetailType.values();
        String[] names = new String[types.length - 1]; //Ignore last DetailType, since this is DetailType.UNDEFINED.
        for (int i = 0; i < names.length; i++) {
            names[i] = types[i].getDisplayName();
        }
        return names;
    }


    /**
     * Method returns the type based on the passed type name.
     *
     * @param typeName  Display name of the type to be returned.
     * @return          Type of the passed display name.
     */
    public static DetailType GET_TYPE_BY_NAME(String typeName) {
        for (DetailType type : DetailType.values()) {
            if (type.getDisplayName().equals(typeName)) {
                return type;
            }
        }
        return DetailType.UNDEFINED;
    }


    /**
     * Method copies all attributes from the passed {@link Detail} to this instance.
     *
     * @param detail    Detail whose attributes shall be copied to this instance.
     */
    private void copyAttributesFromDetail(@NonNull Detail detail) {
        this.uuid = detail.getUuid();
        this.name = detail.getName();
        this.content = detail.getContent();
        this.created = detail.getCreated();
        this.changed = detail.getChanged();
        this.type = detail.getType();
        this.visible = detail.isVisible();
        this.obfuscated = detail.isObfuscated();
        this.encrypted = detail.isEncrypted();
    }

}
