package de.christian2003.core.conversion.csv


/**
 * Class implements the configuration for CSV.
 *
 * @param columnDivider     Character used to divide columns.
 * @param rowDivider        Character used to divide rows.
 * @param stringSeparator   Character used to separate strings.
 * @author                  Christian-2003
 * @since                   3.8.0
 */
data class CsvConfig(

    /**
     * Character used to divide columns.
     */
    val columnDivider: Char = ',',

    /**
     * Character used to divide rows.
     */
    val rowDivider: Char = '\n',

    /**
     * Character used to separate strings.
     */
    val stringSeparator: Char = '"'

)
