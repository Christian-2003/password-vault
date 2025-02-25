package de.christian2003.core.conversion.csv


/**
 * Class implements a parser which can parse a row of CSV content.
 *
 * @param config    Config to use when parsing the CSV.
 * @param csv       CSV to parse.
 * @author          Christian-2003
 * @since           3.8.0
 */
class CsvParser(

    /**
     * CSV to parse.
     */
    private val csv: String,

    /**
     * Config to use when parsing the CSV.
     */
    private val config: CsvConfig = CsvConfig()

) {

    /**
     * Attribute stores the current index within the CSV.
     */
    private var currentIndex: Int = 0


    /**
     * Parses the next column of the CSV. The column content will be returned as string (or null if
     * the content of the column is null). If no more columns are available, null is returned.
     *
     * @return  Content of the next column in the CSV.
     */
    fun next(): String? {
        if (currentIndex >= csv.length) {
            return null
        }
        var containsSpecialCharacters = false
        if (csv[currentIndex] == config.stringSeparator) {
            containsSpecialCharacters = true
            currentIndex++
        }
        val column = StringBuilder()
        var i = currentIndex
        while (i < csv.length) {
            when (val char: Char = csv[i]) {
                config.columnDivider, config.rowDivider, config.stringSeparator -> {
                    if (containsSpecialCharacters && char != config.stringSeparator) {
                        column.append(char)
                    }
                    else {
                        //Add (i + 2) to index if the current char is string separator, since char
                        //after string separator must always be a column divider:
                        currentIndex = if (char == config.stringSeparator) { i + 2 } else { i + 1 }
                        return if (column.toString() == "\u0000") {
                            null
                        } else {
                            column.toString()
                        }
                    }
                }
                '\\' -> {
                    if (csv.length - 1 > i + 1) {
                        when (csv[++i]) {
                            'n' -> {
                                column.append(config.rowDivider)
                            }
                            config.stringSeparator -> {
                                column.append(config.stringSeparator)
                            }
                            else -> {
                                column.append(char)
                                i--
                            }
                        }
                    }
                    else {
                        column.append(char)
                    }
                }
                else -> {
                    column.append(char)
                }
            }
            i++
        }

        //Parsed last column in the last row
        currentIndex = csv.length
        return if (column.toString() == "\u0000") {
            null
        } else {
            column.toString()
        }
    }


    /**
     * Returns whether the CSV has a next column to parse.
     *
     * @return  Whether the CSV has a next column to parse.
     */
    fun hasNext(): Boolean {
        return currentIndex < csv.length
    }

}
