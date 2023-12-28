package de.passwordvault.model.storage.csv;

import java.util.ArrayList;


/**
 * Class implements a parser which can parse CSV (that was generated through {@link CsvBuilder}).
 *
 * @author  Christian-2003
 * @version 3.0.0
 */
public class CsvParser extends CsvConfiguration {

    /**
     * Attribute stores the parsed CSV columns.
     */
    private final ArrayList<String> columns;

    /**
     * Attribute stores the CSV which shall be parsed.
     */
    private final String csv;

    /**
     * Attribute stores the current index within the parsed CSV.
     */
    private int currentIndex;


    /**
     * Constructor instantiates a new {@link CsvParser}.
     *
     * @param csv                   CSV which shall be parsed.
     * @throws NullPointerException The passed CSV is {@code null}.
     */
    public CsvParser(String csv) throws NullPointerException {
        if (csv == null) {
            throw new NullPointerException("Null is invalid CSV");
        }
        this.csv = csv;
        columns = new ArrayList<>();
        currentIndex = 0;
    }


    /**
     * Method parses the provided CSV. The CSV will be converted into an array list of strings.
     *
     * @return  List of string-contents of the CSV columns.
     */
    public ArrayList<String> parseCsv() {
        columns.clear();
        while (currentIndex < csv.length()) {
            next();
        }
        return columns;
    }


    /**
     * Method parses the next column of the passed CSV and appends the parsed value to
     * {@link #columns}.
     */
    private void next() {
        boolean containsSpecialCharacters = false;
        if (csv.charAt(currentIndex) == STRING_SEPARATOR) {
            containsSpecialCharacters = true;
            currentIndex++;
        }
        StringBuilder column = new StringBuilder();
        for (int i = currentIndex; i < csv.length(); i++) {
            char currentChar = csv.charAt(i);
            switch (currentChar) {
                case STRING_SEPARATOR:
                case COLUMN_DIVIDER:
                case ROW_DIVIDER:
                    if (containsSpecialCharacters && currentChar != STRING_SEPARATOR) {
                        column.append(currentChar);
                    }
                    else {
                        columns.add(column.toString());
                        //Add (i + 2) to index if the current char is string separator, since
                        //character after string separator must always be a column divider:
                        currentIndex = currentChar == STRING_SEPARATOR ? i + 2 : i + 1;
                        return;
                    }
                    break;
                case '\\':
                    if (csv.length() - 1 > i + 1) {
                        char escapedChar = csv.charAt(++i);
                        if (escapedChar == 'n') {
                            column.append(ROW_DIVIDER);
                        }
                        else if (escapedChar == STRING_SEPARATOR) {
                            column.append(STRING_SEPARATOR);
                        }
                        else {
                            column.append(currentChar);
                            i--;
                        }
                    }
                    else {
                        column.append(currentChar);
                    }
                    break;
                default:
                    column.append(currentChar);
            }
        }
        //Parsed the last column in the last row:
        currentIndex = csv.length();
        columns.add(column.toString());
    }

}
