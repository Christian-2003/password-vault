package de.christian2003.core.conversion.csv


/**
 * Class implements the configuration for CSV.
 *
 * @param columnDivider     Character used to divide columns.
 * @param rowDivider        Character used to divide rows.
 * @param stringSeparator   Character used to separate strings.
 */
data class CsvConfig(

    val columnDivider: Char = ',',

    val rowDivider: Char = '\n',

    val stringSeparator: Char = '"'

)
