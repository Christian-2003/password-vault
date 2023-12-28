package de.passwordvault.model.entry;

import java.util.ArrayList;
import java.util.Calendar;
import de.passwordvault.model.storage.csv.CsvBuilder;
import de.passwordvault.model.storage.csv.CsvParser;


/**
 * Class models a Data Transfer Object for the {@link Entry}-class. This class is used to transfer
 * data between primary and secondary storage.
 *
 * @author  Christian-2003
 * @version 3.0.0
 */
public class EntryDTO {

    /**
     * Attribute stores the UUID of the entry as a String.
     */
    private String uuid;

    /**
     * Attribute stores the name of the entry as a String.
     */
    private String name;

    /**
     * Attribute stores the description of the entry as a String.
     */
    private String description;

    /**
     * Attribute stores the date and time on which the entry was created in millis. Can be
     * retrieved through {@linkplain Calendar#getTimeInMillis()}.
     */
    private long createdMillis;

    /**
     * Attribute stores the date and time on which the entry was created in millis. Can be
     * retrieved through {@linkplain Calendar#getTimeInMillis()}.
     */
    private long changedMillis;

    /**
     * Attribute stores whether the entry is visible or not.
     */
    private boolean visible;


    /**
     * Constructor instantiates a new {@link EntryDTO} from the passed {@link Entry}. The
     * constructor will assure that all attributes for the DTO will always be instantiated correctly.
     *
     * @param entry                 Entry from which to create this DTO.
     * @throws NullPointerException The passed entry or its UUID is {@code null}.
     */
    public EntryDTO(Entry entry) throws NullPointerException {
        if (entry == null) {
            throw new NullPointerException("Null is invalid entry");
        }

        uuid = entry.getUuid();
        if (uuid == null) {
            throw new NullPointerException("Null is invalid UUID for entry");
        }

        name = entry.getName();
        if (name == null) {
            name = "";
        }

        description = entry.getDescription();
        if (description == null) {
            description = "";
        }

        Calendar created = entry.getCreated();
        if (created != null) {
            createdMillis = created.getTimeInMillis();
        }
        else {
            createdMillis = 0;
        }

        Calendar changed = entry.getChanged();
        if (changed != null) {
            changedMillis = changed.getTimeInMillis();
        }
        else {
            changedMillis = 0;
        }

        visible = entry.isVisible();
    }

    /**
     * Constructor instantiates a new {@link EntryDTO} from the passed line of CSV. The constructor
     * will assure, that all attributes for the DTO will always be instantiated correctly. Please
     * assure that the passed CSV is of the same format that is generated through {@link #getCsv()}.
     *
     * @param csv                   CSV from which to generate this DTO.
     * @throws NullPointerException The passed CSV is {@code null}.s
     */
    public EntryDTO(String csv) throws NullPointerException {
        if (csv == null) {
            throw new NullPointerException("Null is invalid CSV");
        }

        //Set some default values so that all attributes are instantiated in case some value is missing
        //in the passed CSV:
        uuid = "";
        name = "";
        description = "";
        createdMillis = 0;
        changedMillis = 0;
        visible = false;

        //Parse the passed CSV:
        parseCsv(csv);
    }


    /**
     * Method returns a CSV representation of this DTO in the following format:
     * {@code <uuid>,<name>,<description>,<created>,<changed>,<visible>}.
     *
     * @return  CSV representation of this DTO.
     * @see     CsvBuilder
     */
    public String getCsv() {
        CsvBuilder builder = new CsvBuilder();
        builder.append(uuid);
        builder.append(name);
        builder.append(description);
        builder.append(createdMillis);
        builder.append(changedMillis);
        builder.append(visible);
        return builder.toString();
    }


    /**
     * Method converts this DTO into an {@link Entry} instance.
     *
     * @return  Converted entry.
     */
    public Entry getEntry() {
        Calendar created = Calendar.getInstance();
        created.setTimeInMillis(createdMillis);
        Calendar changed = Calendar.getInstance();
        changed.setTimeInMillis(changedMillis);
        Entry entry = new Entry(uuid, name, description);
        entry.setCreated(created);
        entry.setChanged(changed);
        entry.setVisible(visible);
        return entry;
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
                name = columnContent;
                break;
            case 2:
                description = columnContent;
                break;
            case 3:
                try {
                    createdMillis = Long.parseLong(columnContent);
                }
                catch (NumberFormatException e) {
                    //Do nothing, since constructor already set default value.
                }
                break;
            case 4:
                try {
                    changedMillis = Long.parseLong(columnContent);
                }
                catch (NumberFormatException e) {
                    //Do nothing, since constructor already set default value.
                }
                break;
            case 5:
                try {
                    visible = Boolean.parseBoolean(columnContent);
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
