package de.passwordvault.model.storage.csv;

import androidx.annotation.NonNull;


/**
 * Class models a builder which can build CSV.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class CsvBuilder extends CsvConfiguration {

    /**
     * Attribute stores the StringBuilder which is used internally to build the CSV.
     */
    private final StringBuilder csv;

    /**
     * Attribute stores whether {@link #csv} already contains some content.
     */
    private boolean containsContent;


    /**
     * Constructor instantiates a new {@link CsvBuilder}-instance.
     */
    public CsvBuilder() {
        csv = new StringBuilder();
        containsContent = false;
    }


    /**
     * Method appends the passed argument to the generated CSV.
     *
     * @param arg   Value to be appended to the CSV.
     */
    public void append(int arg) {
        if (containsContent) {
            csv.append(COLUMN_DIVIDER);
        }
        csv.append(arg);
        containsContent = true;
    }

    /**
     * Method appends the passed argument to the generated CSV.
     *
     * @param arg   Value to be appended to the CSV.
     */
    public void append(byte arg) {
        if (containsContent) {
            csv.append(COLUMN_DIVIDER);
        }
        csv.append(arg);
        containsContent = true;
    }

    /**
     * Method appends the passed argument to the generated CSV.
     *
     * @param arg   Value to be appended to the CSV.
     */
    public void append(short arg) {
        if (containsContent) {
            csv.append(COLUMN_DIVIDER);
        }
        csv.append(arg);
        containsContent = true;
    }

    /**
     * Method appends the passed argument to the generated CSV.
     *
     * @param arg   Value to be appended to the CSV.
     */
    public void append(long arg) {
        if (containsContent) {
            csv.append(COLUMN_DIVIDER);
        }
        csv.append(arg);
        containsContent = true;
    }

    /**
     * Method appends the passed argument to the generated CSV.
     *
     * @param arg   Value to be appended to the CSV.
     */
    public void append(float arg) {
        if (containsContent) {
            csv.append(COLUMN_DIVIDER);
        }
        csv.append(arg);
        containsContent = true;
    }

    /**
     * Method appends the passed argument to the generated CSV.
     *
     * @param arg   Value to be appended to the CSV.
     */
    public void append(double arg) {
        if (containsContent) {
            csv.append(COLUMN_DIVIDER);
        }
        csv.append(arg);
        containsContent = true;
    }

    /**
     * Method appends the passed argument to the generated CSV.
     *
     * @param arg   Value to be appended to the CSV.
     */
    public void append(char arg) {
        if (containsContent) {
            csv.append(COLUMN_DIVIDER);
        }
        csv.append(arg);
        containsContent = true;
    }

    /**
     * Method appends the passed argument to the generated CSV.
     *
     * @param arg   Value to be appended to the CSV.
     */
    public void append(boolean arg) {
        if (containsContent) {
            csv.append(COLUMN_DIVIDER);
        }
        csv.append(arg);
        containsContent = true;
    }

    /**
     * Method appends the passed argument to the generated CSV. Passing {@code null} is allowed,
     * but not recommended.
     *
     * @param arg   Value to be appended to the CSV.
     */
    public void append(String arg) {
        if (containsContent) {
            csv.append(COLUMN_DIVIDER);
        }
        if (arg == null) {
            csv.append('\0');
            containsContent = true;
            return;
        }
        boolean containsSpecialCharacters = false;
        StringBuilder string = new StringBuilder(); //I'm funny I know...
        for (int i = 0; i < arg.length(); i++) {
            char currentChar = arg.charAt(i);
            switch (currentChar) {
                case STRING_SEPARATOR:
                    string.append("\\"); //Escape string separator for CSV structure.
                    string.append(currentChar);
                    containsSpecialCharacters = true;
                    break;
                case COLUMN_DIVIDER:
                    string.append(currentChar);
                    containsSpecialCharacters = true;
                    break;
                case ROW_DIVIDER:
                    string.append("\\n"); //Escape row divider.
                    containsSpecialCharacters = true;
                    break;
                default:
                    string.append(currentChar);
            }
        }
        if (containsSpecialCharacters) {
            csv.append(STRING_SEPARATOR);
        }
        csv.append(string);
        if (containsSpecialCharacters) {
            csv.append(STRING_SEPARATOR);
        }
        containsContent = true;
    }

    /**
     * Method appends the passed argument to the generated CSV. Passing {@code null} is allowed,
     * but not recommended.
     *
     * @param arg   Value to be appended to the CSV.
     */
    public void append(Object arg) {
        if (arg == null) {
            if (containsContent) {
                append(COLUMN_DIVIDER);
            }
            append('\0');
            containsContent = true;
        }
        else {
            append(arg.toString());
        }
    }


    /**
     * Method appends a line feed to the generated CSV.
     */
    public void newLine() {
        csv.append(ROW_DIVIDER);
        containsContent = false;
    }


    /**
     * Method converts the generated CSV into a string.
     *
     * @return  Generated CSV.
     */
    @NonNull
    @Override
    public String toString() {
        return csv.toString();
    }

}
