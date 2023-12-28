package de.passwordvault.view.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;


/**
 * Class provides some useful methods that are needed in multiple situations. However, it is not
 * necessary to implement each of the provided methods with it's own class.
 *
 * @author  Christian-2003
 * @version 3.0.0
 */
public class Utils {

    /**
     * Static method formats the passed {@code Calendar} instance into a date of the specified format.
     * The format might be e.g. {@code yyyy-MM-dd}.
     *
     * @param date      Date to be formatted.
     * @param format    Desired format.
     * @return          Formatted date.
     */
    public static String formatDate(Calendar date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return formatter.format(LocalDateTime.of(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE)));
    }


    /**
     * Static method replaces every character in the passed String with an asterisk ('*') to
     * obfuscate the content of the String. If the passed argument is {@code null}, {@code ""} will
     * be returned.
     *
     * @param s String to be obfuscated.
     * @return  Obfuscated String.
     */
    public static String obfuscate(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            builder.append('*');
        }
        return builder.toString();
    }

}
