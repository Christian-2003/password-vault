package de.passwordvault.view.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.analysis.QualityGateManager;


/**
 * Class provides some useful methods that are needed in multiple situations. However, it is not
 * necessary to implement each of the provided methods with it's own class.
 *
 * @author  Christian-2003
 * @version 3.7.0
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
     * Static method formats the passed {@code Calendar} instance into a formatted date of the current
     * locale. The format might be e.g. {@code yyyy-MM-dd}.
     *
     * @param date      Date to be formatted.
     * @return          Formatted date.
     */
    public static String formatDate(Calendar date) {
        return formatDate(date, App.getContext().getString(R.string.date_format));
    }


    /**
     * Static method formats the passed number into the format of the current locale.
     *
     * @param number    Number to be formatted.
     * @return          Formatted number.
     */
    public static String formatNumber(double number) {
        NumberFormat formatter = DecimalFormat.getInstance(Locale.getDefault());
        formatter.setMaximumFractionDigits(2);
        return formatter.format(number);
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
            builder.append('•'); // 'U+2022'
        }
        return builder.toString();
    }


    /**
     * Method returns the color for the specified password security score.
     *
     * @param score Password security score for which the color shall be returned.
     * @return      Color for the passed security score.
     */
    public static int getPasswordSecurityScoreColor(int score) {
        double securityScorePercentage = (double)score / (double)QualityGateManager.getInstance().numberOfQualityGates();
        if (securityScorePercentage < 0.33) {
            return App.getContext().getColor(R.color.red);
        }
        else if (securityScorePercentage > 0.67) {
            return App.getContext().getColor(R.color.green);
        }
        else {
            return App.getContext().getColor(R.color.yellow);
        }
    }


    /**
     * Method copies the passed string to the clipboard.
     *
     * @param s String to be copied to the clipboard.
     */
    public static void copyToClipboard(String s) {
        ClipboardManager clipboard = (ClipboardManager)App.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", s);
        clipboard.setPrimaryClip(clip);
    }


    /**
     * Method reads a raw resource file and returns it's content as string.
     *
     * @param id    ID of the resource file to read.
     * @return      Content of the resource file as string.
     */
    public static String readRawResource(int id) {
        byte[] content;
        try (InputStream inputStream = App.getContext().getResources().openRawResource(id)) {
            content = new byte[inputStream.available()];
            inputStream.read(content);
        }
        catch (Exception e) {
            return null;
        }
        return new String(content);
    }

    /**
     * Method reads a raw resource file and returns it's content as string.
     *
     * @param name  Name of the raw resource file to read (without extension).
     * @return      Content of the resource file as string.
     */
    public static String readRawResource(@NonNull String name) {
        byte[] content;
        Resources resources = App.getContext().getResources();
        try (InputStream inputStream = resources.openRawResource(resources.getIdentifier(name, "raw", App.getContext().getPackageName()))) {
            content = new byte[inputStream.available()];
            inputStream.read(content);
        }
        catch (Exception e) {
            return null;
        }
        return new String(content);
    }

}
