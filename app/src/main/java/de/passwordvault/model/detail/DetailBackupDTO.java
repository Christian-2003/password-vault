package de.passwordvault.model.detail;

import java.util.ArrayList;
import java.util.Calendar;
import de.passwordvault.model.storage.app.StorageException;
import de.passwordvault.model.storage.csv.CsvBuilder;
import de.passwordvault.model.storage.csv.CsvParser;


/**
 * Class models a data transfer object for a detail which can be used when storing details within
 * XML backups.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class DetailBackupDTO extends Detail {

    /**
     * Attribute stores the UUID of the entry of which the detail is a part of.
     */
    private String entryUuid;


    /**
     * Constructor instantiates a new detail backup DTO from the specified string-representation.
     * Make sure that the passed string was generated through {@link #toStorable()} beforehand.
     *
     * @param storable              String-representation from which to create this DTO.
     * @throws NullPointerException The passed argument is {@code null}.
     * @throws StorageException     The passed string-representation could not be parsed.
     */
    public DetailBackupDTO(String storable) throws NullPointerException, StorageException {
        super();
        setEntryUuid("");
        fromStorable(storable);
    }

    /**
     * Constructor instantiates a new detail backup DTO for the passed detail and entry-UUID.
     *
     * @param detail                Detail for which the DTO shall be created.
     * @param uuid                  UUID of the entry with which the passed detail is associated.
     * @throws NullPointerException One of the passed arguments is {@code null}.
     */
    public DetailBackupDTO(Detail detail, String uuid) throws NullPointerException {
        super(detail);
        setEntryUuid(uuid);
    }


    /**
     * Method returns the UUID of the entry with which this detail is associated.
     *
     * @return  UUID of the entry of the detail.
     */
    public String getEntryUuid() {
        return entryUuid;
    }

    /**
     * Method returns the UUID of the entry with which this detail is associated.
     *
     * @param uuid                  UUID of the detail's entry.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    public void setEntryUuid(String uuid) throws NullPointerException {
        if (uuid == null) {
            throw new NullPointerException();
        }
        entryUuid = uuid;
    }


    /**
     * Method converts this data transfer object into a {@link Detail}-instance.
     *
     * @return  Detail-instance for this data transfer object.
     */
    public Detail toDetail() {
        return new Detail(this);
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
        builder.append(entryUuid);
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
                        setEntryUuid(cell);
                        break;
                    case 2:
                        setName(cell);
                        break;
                    case 3:
                        setContent(cell);
                        break;
                    case 4:
                        Calendar created = Calendar.getInstance();
                        created.setTimeInMillis(Long.parseLong(cell));
                        setCreated(created);
                        break;
                    case 5:
                        Calendar changed = Calendar.getInstance();
                        changed.setTimeInMillis(Long.parseLong(cell));
                        setChanged(changed);
                        break;
                    case 6:
                        byte typePersistentId = Byte.parseByte(cell);
                        for (DetailType type : DetailType.values()) {
                            if (type.getPersistentId() == typePersistentId) {
                                setType(type);
                                break;
                            }
                        }
                        break;
                    case 7:
                        setVisible(Boolean.parseBoolean(cell));
                        break;
                    case 8:
                        setObfuscated(Boolean.parseBoolean(cell));
                        break;
                }
            }
            catch (Exception e) {
                throw new StorageException("Storage corrupted");
            }
        }
    }


    /**
     * Method returns a CSV-representation of the attribute names that are generated when calling
     * {@link #toStorable()}.
     *
     * @return  CSV-representation of the attribute names.
     */
    public static String getStorableAttributes() {
        CsvBuilder builder = new CsvBuilder();

        builder.append("UUID");
        builder.append("EntryUUID");
        builder.append("Name");
        builder.append("Content");
        builder.append("Created");
        builder.append("Edited");
        builder.append("TypeID");
        builder.append("IsVisible");
        builder.append("IsObfuscated");

        return builder.toString();
    }

}