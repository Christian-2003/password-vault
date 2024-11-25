package de.passwordvault.model.analysis;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;
import de.passwordvault.model.storage.app.Storable;
import de.passwordvault.model.storage.app.StorageException;
import de.passwordvault.model.storage.csv.CsvBuilder;
import de.passwordvault.model.storage.csv.CsvParser;


/**
 * Class models a quality gate for a password. A quality gate consists of a regex which a password
 * must match in order to pass a quality gate. There can be any number of quality gates.
 *
 * @author  Christian-2003
 * @version 3.5.4
 */
public class QualityGate implements Serializable, Storable {

    /**
     * Attribute stores the regex of the quality gate.
     */
    @NonNull
    private String regex;

    /**
     * Attribute stores the description of the quality gate.
     */
    @NonNull
    private String description;

    /**
     * Attribute stores the author for the quality gate. {@code null} indicates that the user is the
     * author.
     */
    @Nullable
    private String author;

    /**
     * Attribute stores whether the quality gate is enabled.
     */
    private boolean enabled;

    /**
     * Attribute stores whether the quality gate is editable. This shall be {@code true} for all
     * quality gates created by the user and {@code false} for all default quality gates.
     */
    private final boolean editable;


    /**
     * Constructor instantiates a new {@code QualityGate} without any content.
     */
    public QualityGate() {
        this.regex = "";
        this.description = "";
        this.enabled = true;
        this.editable = true;
        this.author = null;
    }

    /**
     * Constructor instantiates a new {@code QualityGate} without the passed arguments.
     *
     * @param regex                 Regex for the quality gate.
     * @param description           Description for the quality gate.
     * @param enabled               Whether the quality gate is enabled.
     * @param editable              Whether the quality gate is editable.
     */
    public QualityGate(@NonNull String regex, @NonNull String description, boolean enabled, boolean editable) {
        this.regex = regex;
        this.description = description;
        this.enabled = enabled;
        this.editable = editable;
        this.author = null;
    }

    /**
     * Constructor instantiates a new {@code QualityGate} without the passed arguments.
     *
     * @param regex                 Regex for the quality gate.
     * @param description           Description for the quality gate.
     * @param enabled               Whether the quality gate is enabled.
     * @param editable              Whether the quality gate is editable.
     * @param author                Author for the quality gate. Pass {@code null} to indicate that
     *                              the user is the author.
     */
    public QualityGate(@NonNull String regex, @NonNull String description, boolean enabled, boolean editable, @Nullable String author) {
        this.regex = regex;
        this.description = description;
        this.enabled = enabled;
        this.editable = editable;
        this.author = author;
    }


    /**
     * Method returns the regex of the quality gate.
     *
     * @return  Regex of the quality gate.
     */
    @NonNull
    public String getRegex() {
        return regex;
    }

    /**
     * Method changes the regex of the quality gate to the passed argument.
     *
     * @param regex New regex for the quality gate.
     */
    public void setRegex(@NonNull String regex) {
        this.regex = regex;
    }

    /**
     * Method returns the description of the quality gate.
     *
     * @return  Description of the quality gate.
     */
    @NonNull
    public String getDescription() {
        return description;
    }

    /**
     * Method changes the description of the quality gate to the passed argument.
     *
     * @param description   New description for the quality gate.
     */
    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    /**
     * Method returns whether the quality gate is enabled or not.
     *
     * @return  Whether the quality gate is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Method changes whether the quality gate is enabled or not.
     *
     * @param enabled   Whether the quality gate shall be enabled or not.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Method returns whether the quality gate is editable or not.
     *
     * @return  Whether the quality gate is editable or not.
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Method changes the author for the quality gate. Pass {@code null} to indicate that the
     * current user is the author.
     *
     * @param author    Author for the quality gate.
     */
    public void setAuthor(@Nullable String author) {
        this.author = author;
    }

    /**
     * Method returns the author of the quality gate. {@code null} indicates that the user is the
     * author.
     *
     * @return  Author for the quality gate.
     */
    @Nullable
    public String getAuthor() {
        return author;
    }


    /**
     * Method tests whether the passed argument matches the quality gate. If so, {@code true} is
     * returned. If the passed argument does not match the quality gate, {@code false} is returned.
     *
     * @param s String to be tested.
     * @return  Whether the argument matches the quality gate.
     */
    public boolean matches(@Nullable String s) {
        if (s == null) {
            return false;
        }
        try {
            return s.matches(regex);
        }
        catch (PatternSyntaxException e) {
            return false;
        }
    }


    /**
     * Method converts this instance into a string-representation which can be used for persistent
     * storage. The generated string can later be parsed into a storable through
     * {@link #fromStorable(String)}.
     *
     * @return  String-representation of this instance.
     */
    @Override
    public String toStorable() {
        CsvBuilder builder = new CsvBuilder();
        builder.append(getRegex());
        builder.append(getDescription());
        builder.append(isEnabled());
        builder.append(getAuthor());
        return builder.toString();
    }

    /**
     * Method creates the instance from it's passed string-representation. The passed string - which
     * is a storable's string-representation - must be generated by {@link #toStorable()} beforehand.
     *
     * @param s                     String-representation from which to create the instance.
     * @throws StorageException     The passed string could not be converted into an instance.
     * @throws NullPointerException The passed string is {@code null}.
     */
    @Override
    public void fromStorable(String s) throws StorageException, NullPointerException {
        if (s == null) {
            throw new NullPointerException();
        }
        CsvParser parser = new CsvParser(s);
        ArrayList<String> columns = parser.parseCsv();
        for (int i = 0; i < columns.size(); i++) {
            String cell = columns.get(i);
            try {
                switch (i) {
                    case 0:
                        setRegex(cell);
                        break;
                    case 1:
                        setDescription(cell);
                        break;
                    case 2:
                        setEnabled(Boolean.parseBoolean(cell));
                        break;
                    case 3:
                        author = cell;
                        break;
                }
            }
            catch (Exception e) {
                throw new StorageException(e.getMessage());
            }
        }
    }

}
