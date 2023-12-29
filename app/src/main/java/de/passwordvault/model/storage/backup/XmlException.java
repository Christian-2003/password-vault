package de.passwordvault.model.storage.backup;


/**
 * Class models an {@link Exception} which can be thrown when some error is encountered regarding
 * XML.
 *
 * @author  Christian-2003
 * @version 3.1.0
 */
public class XmlException extends Exception {

    /**
     * Constructor instantiates a new {@link Exception} which can be thrown when something goes
     * wrong regarding XML.
     *
     * @param message   Error message.
     */
    public XmlException(String message) {
        super(message);
    }

}
