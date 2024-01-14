package de.passwordvault.view.utils;

/**
 * Class models an {@linkplain Exception} which can be thrown when something regarding a
 * {@linkplain androidx.fragment.app.DialogFragment}'s arguments goes wrong.
 * This exception is intended to be thrown when arguments are missing or invalid.
 * @author  Christian-2003
 * @version 3.2.0
 */
public class DialogArgumentException extends Exception {

    /**
     * Constructor instantiates a new {@linkplain Exception} with the provided message.
     *
     * @param message   Message to be delivered alongside the Exception.
     */
    public DialogArgumentException(String message) {
        super(message);
    }

}
