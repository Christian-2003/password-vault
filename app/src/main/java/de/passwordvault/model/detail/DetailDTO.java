package de.passwordvault.model.detail;

import java.util.ArrayList;
import java.util.Calendar;
import de.passwordvault.model.storage.csv.CsvBuilder;
import de.passwordvault.model.storage.csv.CsvParser;


/**
 * Class models a Data Transfer Object for the {@link Detail}-class. This class is used to transfer
 * data between primary and secondary storage.
 *
 * @author  Christian-2003
 * @version 3.0.0
 */
public class DetailDTO {

    /**
     * Attribute stores the UUID of the detail as a string.
     */
    private String uuid;

    /**
     * Attribute stores the UUID of the {@link de.passwordvault.model.entry.Entry}, to which this
     * detail belongs.
     */
    private String entryUuid;

    /**
     * Attribute stores the name of the detail.
     */
    private String name;

    /**
     * Attribute stores the context of the detail.
     */
    private String content;

    /**
     * Attribute stores the date and time on which the detail was created in millis. Can be
     * retrieved through {@linkplain Calendar#getTimeInMillis()}.
     */
    private long createdMillis;

    /**
     * Attribute stores the date and time on which the detail was changed in millis. Can be
     * retrieved through {@linkplain Calendar#getTimeInMillis()}.
     */
    private long changedMillis;

    /**
     * Attribute stores the {@link DetailType#getPersistentId()} of the type of the detail.
     */
    private byte detailTypePersistentId;

    /**
     * Attribute stores whether the detail is visible or not.
     */
    private boolean visible;

    /**
     * Attribute stores whether the detail is obfuscated or not.
     */
    private boolean obfuscated;


    /**
     * Constructor instantiates a new {@link DetailDTO} from the passed {@link Detail}. The
     * constructor will assure that all attributes for the DTO will always be instantiated correctly.
     *
     * @param detail                Detail from which to create this DTO.
     * @param entryUuid             UUID of the entry to which this detail belongs.
     * @throws NullPointerException The passed detail or its UUID is {@code null}.
     */
    public DetailDTO(Detail detail, String entryUuid) throws NullPointerException {
        if (detail == null) {
            throw new NullPointerException("Null is invalid detail");
        }

        uuid = detail.getUuid();
        if (uuid == null) {
            throw new NullPointerException("Null is invalid UUID for DetailDTO");
        }

        name = detail.getName();
        if (name == null) {
            name = "";
        }

        content = detail.getContent();
        if (content == null) {
            content = "";
        }

        Calendar created = detail.getCreated();
        if (created != null) {
            createdMillis = created.getTimeInMillis();
        }
        else {
            createdMillis = 0;
        }

        Calendar changed = detail.getChanged();
        if (changed != null) {
            changedMillis = changed.getTimeInMillis();
        }
        else {
            changedMillis = 0;
        }

        DetailType type = detail.getType();
        if (type != null) {
            detailTypePersistentId = type.getPersistentId();
        }
        else {
            detailTypePersistentId = DetailType.UNDEFINED.getPersistentId();
        }

        visible = detail.isVisible();

        obfuscated = detail.isObfuscated();

        if (entryUuid == null) {
            throw new NullPointerException("Null is invalid entry UUID");
        }
        this.entryUuid = entryUuid;
    }

    /**
     * Constructor instantiates a new {@link DetailDTO} from the passed line of CSV. The constructor
     * will assure, that all attributes for the DTO will always be instantiated correctly. Please
     * assure that the passed CSV is of the same format that is generated through {@link #getCsv()}.
     *
     * @param csv                   CSV from which to generate this DTO.
     * @throws NullPointerException The passed CSV is {@code null}.s
     */
    public DetailDTO(String csv) throws NullPointerException {
        if (csv == null) {
            throw new NullPointerException("Null is invalid CSV");
        }

        //Set some default values so that all attributes are instantiated in case some value is missing
        //in the passed CSV:
        entryUuid = "";
        uuid = "";
        name = "";
        content = "";
        createdMillis = 0;
        changedMillis = 0;
        detailTypePersistentId = DetailType.UNDEFINED.getPersistentId();
        visible = false;
        obfuscated = true;

        //Parse the passed CSV:
        parseCsv(csv);
    }


    /**
     * Method returns the UUID of the {@link de.passwordvault.model.entry.Entry} to which this
     * detail belongs.
     *
     * @return  UUID of the entry to which this detail belongs.
     */
    public String getEntryUuid() {
        return entryUuid;
    }


    /**
     * Method returns a CSV representation of this DTO in the following format:
     * {@code <uuid>,<entryUuid>,<name>,<content>,<created>,<changed>,<type>,<visible>,<obfuscated>}.
     *
     * @return  CSV representation of this DTO.
     * @see     CsvBuilder
     */
    public String getCsv() {
        CsvBuilder builder = new CsvBuilder();
        builder.append(uuid);
        builder.append(entryUuid);
        builder.append(name);
        builder.append(content);
        builder.append(createdMillis);
        builder.append(changedMillis);
        builder.append(detailTypePersistentId);
        builder.append(visible);
        builder.append(obfuscated);
        return builder.toString();
    }


    /**
     * Method converts this DTO into a {@link Detail} instance.
     *
     * @return  Converted detail.
     */
    public Detail getDetail() {
        Calendar created = Calendar.getInstance();
        created.setTimeInMillis(createdMillis);
        Calendar changed = Calendar.getInstance();
        changed.setTimeInMillis(changedMillis);
        DetailType type = DetailType.UNDEFINED;
        for (DetailType current : DetailType.values()) {
            if (current.getPersistentId() == detailTypePersistentId) {
                type = current;
                break;
            }
        }
        Detail detail = new Detail(uuid, name, content, created, changed, type);
        detail.setVisible(visible);
        detail.setObfuscated(obfuscated);
        return detail;
    }


    /**
     * Method parses the passed CSV and instantiates the attributes of this instance based on the
     * passed CSV. Please assure that the only CSV that is passed to this method was generated
     * through {@link #getCsv()}.
     *
     * @param csv   CSV that shall be parsed.
     */
    private void parseCsv(String csv) {
        CsvParser parser = new CsvParser(csv);
        ArrayList<String> columns = parser.parseCsv();
        for (int i = 0; i < columns.size(); i++) {
            assignColumnContentToDetail(columns.get(i), i);
        }
    }


    /**
     * Method assigns the passed content to the DTO instance, based on the column of the CSV table in
     * which it was encountered. The method assures that the respective attribute is assigned, no
     * matter the passed content. Therefore, the passed column numbers (beginning with 0) must match
     * the column numbers of CSV that is generated through {@link #getCsv()}.
     *
     * @param columnContent             The content to assign to the attribute of the respective
     *                                  column.
     * @param column                    Number of the column in which the content was encountered.
     * @throws IllegalArgumentException The passed column number has no attribute in which to store
     *                                  the passed content.
     */
    private void assignColumnContentToDetail(String columnContent, int column) throws IllegalArgumentException {
        switch (column) {
            case 0:
                uuid = columnContent;
                break;
            case 1:
                entryUuid = columnContent;
                break;
            case 2:
                name = columnContent;
                break;
            case 3:
                content = columnContent;
                break;
            case 4:
                try {
                    createdMillis = Long.parseLong(columnContent);
                }
                catch (NumberFormatException e) {
                    //Do nothing, since constructor already set default value.
                }
                break;
            case 5:
                try {
                    changedMillis = Long.parseLong(columnContent);
                }
                catch (NumberFormatException e) {
                    //Do nothing, since constructor already set default value.
                }
                break;
            case 6:
                try {
                    detailTypePersistentId = Byte.parseByte(columnContent);
                }
                catch (NumberFormatException e) {
                    //Do nothing, since constructor already set default value.
                }
                break;
            case 7:
                try {
                    visible = Boolean.parseBoolean(columnContent);
                }
                catch (NumberFormatException e) {
                    //Do nothing, since constructor already set default value.
                }
                break;
            case 8:
                try {
                    obfuscated = Boolean.parseBoolean(columnContent);
                }
                catch (NumberFormatException e) {
                    //Do nothing, since constructor already set default value.
                }
                break;
            default:
                throw new IllegalArgumentException(column + " is invalid column number for DetailDTO");
        }
    }

}
