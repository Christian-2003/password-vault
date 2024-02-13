package de.passwordvault.model.detail;

import androidx.annotation.NonNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;
import de.passwordvault.model.Identifiable;
import de.passwordvault.model.storage.app.Storable;
import de.passwordvault.model.storage.app.StorageException;
import de.passwordvault.model.storage.csv.CsvBuilder;
import de.passwordvault.model.storage.csv.CsvParser;


/**
 * Class models a detail which can contain all types of detailed information for an entry.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class Detail implements Identifiable, Storable, Serializable {

    /**
     * Attribute stores the date on which the detail was changed the last time.
     */
    protected Calendar changed;

    /**
     * Attribute stores content of the detail.
     */
    protected String content;

    /**
     * Attribute stores the date on which the detail was created.
     */
    protected Calendar created;

    /**
     * Attribute stores name of the detail.
     */
    protected String name;

    /**
     * Attribute stores whether the details content shall be obfuscated when displayed. This might
     * be used to display passwords as '******' instead of 'abc123'.
     */
    protected boolean obfuscated;

    /**
     * Attribute stores the type of the detail.
     * The type defines the format of the content. This is used to enhance the way that the detail
     * is later displayed within the frontend.
     */
    protected DetailType type;

    /**
     * Attribute stores type 4 UUID of the detail.
     */
    protected String uuid;

    /**
     * Attribute stores whether the detail shall be visible by default.
     */
    protected boolean visible;


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
    }


    /**
     * Method returns the most recent date on which the detail was changed.
     *
     * @return  Most recent date on which the detail was changed.
     */
    public Calendar getChanged() {
        return changed;
    }

    /**
     * Method changes the most recent date on which the detail was changed.
     *
     * @param changed               New most recent date on which the detail was changed.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setChanged(Calendar changed) throws NullPointerException {
        if (changed == null) {
            throw new NullPointerException("Null is invalid date");
        }
        this.changed = changed;
    }

    /**
     * Method returns the content of the detail.
     *
     * @return  Content of the detail.
     */
    public String getContent() {
        return content;
    }

    /**
     * Method changes the content of the detail to the passed argument.
     *
     * @param content               New content for the detail.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setContent(String content) throws NullPointerException {
        if (content == null) {
            throw new NullPointerException("Null is invalid content");
        }
        this.content = content;
    }

    /**
     * Method returns the date on which the detail was created.
     *
     * @return  Date on which the detail was created.
     */
    public Calendar getCreated() {
        return created;
    }

    /**
     * Method changes the date on which the detail was created.
     *
     * @param created               Date on which the detail was created.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setCreated(Calendar created) throws NullPointerException {
        if (created == null) {
            throw new NullPointerException("Null is invalid date");
        }
        this.created = created;
    }

    /**
     * Method returns the name of the detail.
     *
     * @return  Name of the detail.
     */
    public String getName() {
        return name;
    }

    /**
     * Method changes the name of the detail to the passed argument.
     *
     * @param name                  New name for the detail.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setName(String name) throws NullPointerException {
        if (name == null) {
            throw new NullPointerException("Null is invalid name");
        }
        this.name = name;
    }

    /**
     * Method returns whether the detail is obfuscated ot not.
     *
     * @return  Whether the detail is obfuscated or not.
     */
    public boolean isObfuscated() {
        return obfuscated;
    }

    /**
     * Method changes whether the detail is obfuscated or not.
     *
     * @param obfuscated    Whether the detail shall be obfuscated or not.
     */
    public void setObfuscated(boolean obfuscated) {
        this.obfuscated = obfuscated;
    }

    /**
     * Method returns the type of the detail.
     *
     * @return  Type of the detail.
     */
    public DetailType getType() {
        return type;
    }

    /**
     * Method changes the type of the detail.
     *
     * @param type                  New type for the detail.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setType(DetailType type) throws NullPointerException {
        if (type == null) {
            throw new NullPointerException("Null is invalid type");
        }
        this.type = type;
    }

    /**
     * Method returns the UUID of the detail.
     *
     * @return  UUID of the detail.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Method returns whether the detail is visible or not.
     *
     * @return  Whether the detail is visible or not.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Method changes whether the detail is visible or not.
     *
     * @param visible   Whether the detail shall be visible or not.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }


    /**
     * Method tests whether the UUID of the passed detail is identical to the UUID of this detail.
     *
     * @param obj   Detail whose UUID shall be compared to the UUID of this detail.
     * @return      Whether both UUIDs are identical.
     */
    public boolean equals(Identifiable obj) {
        return obj.getUuid().equals(uuid);
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
    }


    /**
     * Static method returns a {@linkplain String} array which contains a String representation
     * for the available types.
     *
     * @return          String-array containing the String representations for the Detail types.
     */
    public static String[] getTypes() {
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
    public static DetailType getTypeByName(String typeName) {
        for (DetailType type : DetailType.values()) {
            if (type.getDisplayName().equals(typeName)) {
                return type;
            }
        }
        return DetailType.UNDEFINED;
    }


    /**
     * Method converts this instance into a string-representation which can be used for persistent
     * storage. The generated string can later be parsed into a storable through
     * {@link #fromStorable(String)}.
     *
     * @return  String-representation of this instance.
     */
    @Override
    public String toStorable() {
        CsvBuilder builder = new CsvBuilder();

        builder.append(uuid);
        builder.append(name);
        builder.append(content);
        builder.append(created.getTimeInMillis());
        builder.append(changed.getTimeInMillis());
        builder.append(type.getPersistentId());
        builder.append(visible);
        builder.append(obfuscated);

        return builder.toString();
    }

    /**
     * Method creates the instance from it's passed string-representation. The passed string - which
     * is a storable's string-representation - must be generated by {@link #toStorable()} beforehand.
     *
     * @param s                     String-representation from which to create the instance.
     * @throws StorageException     The passed string could not be converted into an instance.
     * @throws NullPointerException The passed string is {@code null}.
     */
    @Override
    public void fromStorable(String s) throws StorageException, NullPointerException {
        if (s == null) {
            throw new NullPointerException();
        }
        CsvParser parser = new CsvParser(s);
        ArrayList<String> cells = parser.parseCsv();
        int index = 0;
        for (String cell : cells) {
            try {
                switch (index++) {
                    case 0:
                        uuid = cell;
                        break;
                    case 1:
                        setName(cell);
                        break;
                    case 2:
                        setContent(cell);
                        break;
                    case 3:
                        Calendar created = Calendar.getInstance();
                        created.setTimeInMillis(Long.parseLong(cell));
                        setCreated(created);
                        break;
                    case 4:
                        Calendar changed = Calendar.getInstance();
                        changed.setTimeInMillis(Long.parseLong(cell));
                        setChanged(changed);
                        break;
                    case 5:
                        int typePersistentId = Integer.parseInt(cell);
                        for (DetailType type : DetailType.values()) {
                            if (type.getPersistentId() == typePersistentId) {
                                setType(type);
                                break;
                            }
                        }
                        break;
                    case 6:
                        setVisible(Boolean.parseBoolean(cell));
                        break;
                    case 7:
                        setObfuscated(Boolean.parseBoolean(cell));
                        break;
                }
            }
            catch (Exception e) {
                throw new StorageException("Storage corrupted");
            }
        }
    }

}
