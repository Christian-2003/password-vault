package de.passwordvault.model.analysis;

import java.io.Serializable;
import java.util.regex.PatternSyntaxException;


/**
 * Class models a quality gate for a password. A quality gate consists of a regex which a password
 * must match in order to pass a quality gate. There can be any number of quality gates.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class QualityGate implements Serializable {

    /**
     * Attribute stores the regex of the quality gate.
     */
    private String regex;

    /**
     * Attribute stores the description of the quality gate.
     */
    private String description;

    /**
     * Attribute stores whether the quality gate is enabled.
     */
    private boolean enabled;

    /**
     * Attribute stores whether the quality gate is editable. This shall be {@code true} for all
     * quality gates created by the user and {@code false} for all default quality gates.
     */
    private boolean editable;


    /**
     * Constructor instantiates a new {@code QualityGate} without any content.
     */
    public QualityGate() {
        setRegex("");
        setDescription("");
        setEnabled(true);
        setEditable(true);
    }

    /**
     * Constructor instantiates a new {@code QualityGate} without the passed arguments.
     *
     * @param regex                 Regex for the quality gate.
     * @param description           Description for the quality gate.
     * @param enabled               Whether the quality gate is enabled.
     * @param editable              Whether the quality gate is editable.
     * @throws NullPointerException One of the passed arguments is {@code null}.
     */
    public QualityGate(String regex, String description, boolean enabled, boolean editable) throws NullPointerException {
        setRegex(regex);
        setDescription(description);
        setEnabled(enabled);
        setEditable(editable);
    }


    /**
     * Method returns the regex of the quality gate.
     *
     * @return  Regex of the quality gate.
     */
    public String getRegex() {
        return regex;
    }

    /**
     * Method changes the regex of the quality gate to the passed argument.
     *
     * @param regex                 New regex for the quality gate.
     * @throws NullPointerException The passed regex is {@code null}.
     */
    public void setRegex(String regex) throws NullPointerException {
        if (regex == null) {
            throw new NullPointerException();
        }
        this.regex = regex;
    }

    /**
     * Method returns the description of the quality gate.
     *
     * @return  Description of the quality gate.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Method changes the description of the quality gate to the passed argument.
     *
     * @param description           New description for the quality gate.
     * @throws NullPointerException The passed description is {@code null}.
     */
    public void setDescription(String description) throws NullPointerException {
        if (description == null) {
            throw new NullPointerException();
        }
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
     * Method changes whether the quality gate is editable or not.
     *
     * @param editable  Whether the quality gate shall be editable or not.
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }


    /**
     * Method tests whether the passed argument matches the quality gate. If so, {@code true} is
     * returned. If the passed argument does not match the quality gate, {@code false} is returned.
     *
     * @param s String to be tested.
     * @return  Whether the argument matches the quality gate.
     */
    public boolean matches(String s) {
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

}
