package de.christian2003.core.conversion.csv


/**
 * Class implements a CSV builder that can generate CSV content.
 *
 * @param config    Config determining the syntax of the CSV generated.
 * @author          Christian-2003
 * @since           3.8.0
 */
class CsvBuilder(

    /**
     * Config to use when creating CSV files.
     */
    private val config: CsvConfig = CsvConfig()

) {

    /**
     * Stores the CSV that has been generated so far.
     */
    private val csv: StringBuilder = StringBuilder()

    /**
     * Stores whether the current line in CSV contains any content.
     */
    private var isCurrentLineEmpty: Boolean = true


    /**
     * Appends the argument specified to the current CSV row.
     *
     * @param arg   Value to append to the current CSV row.
     * @return      This CSV builder.
     */
    fun append(arg: Int): CsvBuilder {
        if (!isCurrentLineEmpty) {
            csv.append(config.columnDivider)
        }
        csv.append(arg)
        isCurrentLineEmpty = false
        return this
    }


    /**
     * Appends the argument specified to the current CSV row.
     *
     * @param arg   Value to append to the current CSV row.
     * @return      This CSV builder.
     */
    fun append(arg: Byte): CsvBuilder {
        if (!isCurrentLineEmpty) {
            csv.append(config.columnDivider)
        }
        csv.append(arg)
        isCurrentLineEmpty = false
        return this
    }



    /**
     * Appends the argument specified to the current CSV row.
     *
     * @param arg   Value to append to the current CSV row.
     * @return      This CSV builder.
     */
    fun append(arg: Long): CsvBuilder {
        if (!isCurrentLineEmpty) {
            csv.append(config.columnDivider)
        }
        csv.append(arg)
        isCurrentLineEmpty = false
        return this
    }


    /**
     * Appends the argument specified to the current CSV row.
     *
     * @param arg   Value to append to the current CSV row.
     * @return      This CSV builder.
     */
    fun append(arg: Float): CsvBuilder {
        if (!isCurrentLineEmpty) {
            csv.append(config.columnDivider)
        }
        csv.append(arg)
        isCurrentLineEmpty = false
        return this
    }


    /**
     * Appends the argument specified to the current CSV row.
     *
     * @param arg   Value to append to the current CSV row.
     * @return      This CSV builder.
     */
    fun append(arg: Double): CsvBuilder {
        if (!isCurrentLineEmpty) {
            csv.append(config.columnDivider)
        }
        csv.append(arg)
        isCurrentLineEmpty = false
        return this
    }


    /**
     * Appends the argument specified to the current CSV row.
     *
     * @param arg   Value to append to the current CSV row.
     * @return      This CSV builder.
     */
    fun append(arg: Char): CsvBuilder {
        if (!isCurrentLineEmpty) {
            csv.append(config.columnDivider)
        }
        csv.append(arg)
        isCurrentLineEmpty = false
        return this
    }


    /**
     * Appends the argument specified to the current CSV row.
     *
     * @param arg   Value to append to the current CSV row.
     * @return      This CSV builder.
     */
    fun append(arg: Boolean): CsvBuilder {
        if (!isCurrentLineEmpty) {
            csv.append(config.columnDivider)
        }
        csv.append(arg)
        isCurrentLineEmpty = false
        return this
    }


    /**
     * Appends the string specified to the current CSV row. If the string contains any special
     * characters that are used with the CSV syntax, it is escaped using a string separator.
     *
     * @param arg   String to append to the current CSV row.
     * @return      This CSV builder.
     */
    fun append(arg: String): CsvBuilder {
        if (!isCurrentLineEmpty) {
            csv.append(config.columnDivider)
        }

        var containsSpecialCharacters = false
        val builder = StringBuilder()

        arg.forEach { char ->
            when(char) {
                config.columnDivider -> {
                    builder.append(char)
                    containsSpecialCharacters = true
                }
                config.rowDivider -> {
                    builder.append("\\n")
                    containsSpecialCharacters = true
                }
                config.stringSeparator -> {
                    builder.append("\\")
                    builder.append(char)
                    containsSpecialCharacters = true
                }
                else -> {
                    builder.append(char)
                }
            }
        }

        if (containsSpecialCharacters) {
            csv.append(config.stringSeparator)
        }
        csv.append(builder)
        if (containsSpecialCharacters) {
            csv.append(config.stringSeparator)
        }

        isCurrentLineEmpty = false
        return this
    }


    /**
     * Adds a new line to the CSV.
     *
     * @return  This CSV builder.
     */
    fun newLine(): CsvBuilder {
        csv.append(config.rowDivider)
        isCurrentLineEmpty = true
        return this
    }


    /**
     * Converts the CSV built so far into a string.
     *
     * @return  CSV as string.
     */
    override fun toString(): String {
        return csv.toString()
    }

}
