package de.passwordvault.backend;


import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;

import de.passwordvault.R;
import de.passwordvault.backend.analysis.PasswordSecurity;
import de.passwordvault.backend.entry.Detail;
import de.passwordvault.backend.entry.EntryHandle;
import de.passwordvault.frontend.addentry.DetailViewBuilder;

/**
 * Class implements a Singleton for the activity.
 *
 * @author  Christian-2003
 * @version 1.0.0
 */
public class Singleton {

    /**
     * Static attribute stores the entry handle that is used to handle the entries within this
     * application. This is static as the handled entries are required within every part of the
     * application.
     */
    public static EntryHandle ENTRIES;


    /**
     * Static method formats the passed {@code Calendar} instance into a date of the specified format.
     * The format might be e.g. {@code yyyy-MM-dd}.
     *
     * @param date      Date to be formatted.
     * @param format    Desired format.
     * @return          Formatted date.
     */
    public static String FORMAT_DATE(Calendar date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return formatter.format(LocalDateTime.of(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE)));
    }


    /**
     * Static method replaces every character in the passed String with an asterisk ('*') to
     * obfuscate the content of the String.
     *
     * @param s String to be obfuscated.
     * @return  Obfuscated String.
     */
    public static String OBFUSCATE(String s) {
        StringBuilder builder = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            builder.append('*');
        }
        return builder.toString();
    }


    /**
     * Static method generates a {@linkplain View} for a {@linkplain Detail}.
     *
     * @param context   Context in which the View is used.
     * @param detail    Detail whose View shall be generated.
     * @return          Generated View for the specified Detail.
     * @deprecated      Use {@linkplain DetailViewBuilder} instead!
     */
    public static View GENERATE_DETAIL_VIEW(Context context, Detail detail) {
        DetailViewBuilder builder = new DetailViewBuilder(context, detail);
        return builder.inflate();
    }

}
