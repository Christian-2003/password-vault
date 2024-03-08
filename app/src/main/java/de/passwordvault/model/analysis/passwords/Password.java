package de.passwordvault.model.analysis.passwords;

import java.io.Serializable;
import de.passwordvault.model.analysis.QualityGateManager;


/**
 * Class models a password for the password security analysis.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class Password implements Serializable {

    /**
     * Field stores a value to indicate that no security score has been generated.
     */
    private static final int NO_SECURITY_SCORE_GENERATED = -1;


    /**
     * Attribute stores the password in cleartext.
     */
    private final String cleartextPassword;

    /**
     * Attribute stores the UUID of the entry to which this password belongs.
     */
    private final String entryUuid;

    /**
     * Attribute stores the name of the account of which the password is a part of. The name is stored
     * since it is displayed in the list of analyzed passwords to the user. Accessing the name through
     * {@link de.passwordvault.model.entry.EntryManager} would be possible using the {@link #entryUuid}.
     * However, due to the poor performance of HashMap with string-keys (?), accessing the name this
     * way for a lot of passwords results in a poor app performance and noticeable lag.
     */
    private final String name;

    /**
     * Attribute stores the security score of the password.
     */
    private int cachedSecurityScore;


    /**
     * Constructor instantiates a new password-instance with the passed arguments.
     *
     * @param cleartextPassword     Password in cleartext.
     * @param entryUuid             UUID of the entry to which the password belongs.
     * @throws NullPointerException One of the passed arguments is {@code null}.
     */
    public Password(String cleartextPassword, String entryUuid, String name) throws NullPointerException {
        if (cleartextPassword == null || entryUuid == null) {
            throw new NullPointerException();
        }
        this.cleartextPassword = cleartextPassword;
        this.entryUuid = entryUuid;
        if (name != null) {
            this.name = name;
        }
        else {
            this.name = "";
        }
        cachedSecurityScore = NO_SECURITY_SCORE_GENERATED;
    }


    /**
     * Method returns the password in cleartext.
     *
     * @return  Cleartext password.
     */
    public String getCleartextPassword() {
        return cleartextPassword;
    }

    /**
     * Method returns the UUID of the entry to which the password belongs.
     *
     * @return  UUID of the entry to which the password belongs.
     */
    public String getEntryUuid() {
        return entryUuid;
    }

    /**
     * Method returns the name of the entry of which the password is a part of.
     *
     * @return  Name of the entry of which the password is a part of.
     */
    public String getName() {
        return name;
    }

    /**
     * Method returns the security score for the password generated through
     * {@link QualityGateManager#calculatePassedQualityGates(String)}.
     *
     * @return  Security score for the password.
     */
    public int getSecurityScore() {
        if (cachedSecurityScore == NO_SECURITY_SCORE_GENERATED) {
            cachedSecurityScore = QualityGateManager.getInstance().calculatePassedQualityGates(cleartextPassword);
        }
        return cachedSecurityScore;
    }

}
