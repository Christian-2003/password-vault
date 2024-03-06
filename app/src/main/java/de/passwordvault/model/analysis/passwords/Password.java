package de.passwordvault.model.analysis.passwords;


import de.passwordvault.model.analysis.QualityGateManager;

/**
 * Class models a password for the password security analysis.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class Password {

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
     * Attribute stores the security score of the password.
     */
    private int securityScore;


    /**
     * Constructor instantiates a new password-instance with the passed arguments.
     *
     * @param cleartextPassword     Password in cleartext.
     * @param entryUuid             UUID of the entry to which the password belongs.
     * @throws NullPointerException One of the passed arguments is {@code null}.
     */
    public Password(String cleartextPassword, String entryUuid) throws NullPointerException {
        if (cleartextPassword == null || entryUuid == null) {
            throw new NullPointerException();
        }
        this.cleartextPassword = cleartextPassword;
        this.entryUuid = entryUuid;
        securityScore= NO_SECURITY_SCORE_GENERATED;
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
     * Method returns the security score for the password generated through
     * {@link QualityGateManager#calculatePassedQualityGates(String)}.
     *
     * @return  Security score for the password.
     */
    public int getSecurityScore() {
        if (securityScore == NO_SECURITY_SCORE_GENERATED) {
            securityScore = QualityGateManager.getInstance().calculatePassedQualityGates(cleartextPassword);
        }
        return securityScore;
    }

}
