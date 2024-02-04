package de.passwordvault.model.storage.csv;


/**
 * Class contains all values that are required to format the generated / parsed CSV.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public abstract class CsvConfiguration {

    /**
     * Static field stores the divider that shall be used to separate columns.
     */
    public static final char COLUMN_DIVIDER = ',';

    /**
     * Static field stores the separator that shall be used to separate strings from the CSV.
     */
    public static final char STRING_SEPARATOR = '"';

    /**
     * Static field stores the divider that shall be used to separate rows.
     */
    public static final char ROW_DIVIDER = '\n';

}
