package de.passwordvault.model.storage.backup;

import android.net.Uri;
import de.passwordvault.model.storage.encryption.AES;


/**
 * Class contains the configuration for the XML backup.
 *
 * @author  Christian-2003
 * @version 3.2.0
 */
public abstract class XmlBackupConfiguration {

    /**
     * Field stores the XML tag which encapsulates everything within the XML document.
     */
    protected static final String TAG_PASSWORD_VAULT = "password_vault";

    /**
     * Field stores the XML tag which encapsulates all data regarding the encryption.
     */
    protected static final String TAG_ENCRYPTION = "encryption";

    /**
     * Field stores the XML tag which contains the encrypted {@link #encryptionKeySeed}. This is
     * required during backup restoration to test whether a correct seed was provided.
     */
    protected static final String TAG_ENCRYPTION_CHECKSUM = "checksum";

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


    /**
     * Attribute stores the URI to a file to which the backup shall be created.
     */
    protected final Uri uri;

    /**
     * Attribute stores the instance which can encrypt the backup. This is {@code null} if the backup
     * shall not be encrypted!
     */
    protected final AES encryptionAlgorithm;

    /**
     * Attribute stores the seed which is used to generate the key for encryption / decryption. This
     * shall be {@code null} if the backup is not encrypted.
     */
    protected final String encryptionKeySeed;


    /**
     * Constructor instantiates a new {@link XmlBackupConfiguration}-instance. Please make sure that
     * the application has access (and permission) to write to that file, before calling. If the
     * provided {@code encryptionKeySeed} is not {@code null}, the backup is considered to be
     * encrypted. Otherwise, it is not encrypted.
     *
     * @param uri                   URI of the file for the backup.
     * @param encryptionKeySeed     Seed with which to generate a key for encryption / decryption.
     * @throws NullPointerException The passed URI is {@code null}.
     */
    public XmlBackupConfiguration(Uri uri, String encryptionKeySeed) throws NullPointerException {
        if (uri == null) {
            throw new NullPointerException("Null is invalid URI");
        }
        this.uri = uri;

        this.encryptionKeySeed = encryptionKeySeed;
        if (this.encryptionKeySeed != null) {
            encryptionAlgorithm = new AES(this.encryptionKeySeed);
        }
        else {
            this.encryptionAlgorithm = null;
        }
    }

}