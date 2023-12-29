package de.passwordvault.model.storage.backup;


/**
 * Class contains the configuration for the XML backup.
 *
 * @author  Christian-2003
 * @version 3.1.0
 */
public abstract class XmlBackupConfiguration {

    /**
     * Field stores the XML tag which encapsulates everything within the XML document.
     */
    protected static final String TAG_PASSWORD_VAULT = "password_vault";

    /**
     * Field stores the XML tag which contains the data which is backed up.
     */
    protected static final String TAG_DATA = "data";

    /**
     * Field stores the XML tag which contains the backed up entries.
     */
    protected static final String TAG_ENTRIES = "entries";

    /**
     * Field stores the XML tag which contains the backed up details.
     */
    protected static final String TAG_DETAILS = "details";

}
