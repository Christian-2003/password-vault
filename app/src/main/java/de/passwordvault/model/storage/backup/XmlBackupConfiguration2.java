package de.passwordvault.model.storage.backup;

import android.net.Uri;


/**
 * Class contains the configuration for XML backups of version 2.
 *
 * @author  Christian-2003
 * @version 3.5.4
 */
public class XmlBackupConfiguration2 extends XmlBackupConfiguration {

    /**
     * Field stores the XML tag which contains the backed up settings.
     */
    protected static final String TAG_SETTINGS = "settings";

    /**
     * Field stores the XML tag which contains backed up settings items.
     */
    protected static final String TAG_SETTINGS_ITEM = "item";

    /**
     * Field stores the XML tag which contains the backed up quality gates.
     */
    protected static final String TAG_QUALITY_GATES = "quality_gates";


    /**
     * Field stores the name of the attribute containing the encryption checksum.
     */
    protected static final String ATTRIBUTE_CHECKSUM = "checksum";

    /**
     * Field stores the name of the attribute containing the header for entries and details.
     */
    protected static final String ATTRIBUTE_HEADER = "header";

    /**
     * Field stores the name of the attribute containing the name of a specific setting.
     */
    protected static final String ATTRIBUTE_SETTINGS_NAME = "setting";

    /**
     * Field stores the name of the attribute containing the value of a specific setting.
     */
    protected static final String ATTRIBUTE_SETTINGS_VALUE = "value";


    /**
     * Field stores the name for the setting regarding autofill caching.
     */
    protected static final String SETTING_AUTOFILL_CACHING = "autofill_caching";

    /**
     * Field stores the name for the setting regarding autofill caching.
     */
    protected static final String SETTING_AUTOFILL_AUTHENTICATION = "autofill_authentication";

    /**
     * Field stores the name for the setting regarding darkmode.
     */
    protected static final String SETTING_DARKMODE = "darkmode";

    /**
     * Field stores the name for the setting regarding recently edited entries.
     */
    protected static final String SETTING_NUM_RECENTLY_EDITED = "recently_edited";

    /**
     * Field stores the name for the setting regarding left swiping details.
     */
    protected static final String SETTING_DETAIL_SWIPE_LEFT = "detail_swipe_left";

    /**
     * Field stores the name for the setting regarding right swiping details.
     */
    protected static final String SETTING_DETAIL_SWIPE_RIGHT = "detail_swipe_right";


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
    public XmlBackupConfiguration2(Uri uri, String encryptionKeySeed) throws NullPointerException {
        super(uri, encryptionKeySeed);
    }

    /**
     * Constructor instantiates a new instance. Please make sure that the application has access
     * (and permission) to write to that file, before calling.
     *
     * @param uri                   URI of the file for the backup.
     * @param encrypt               Whether the backup shall be encrypted. If this is {@code true},
     *                              the backup is encrypted with the default key used for the app.
     * @throws NullPointerException The passed URI is {@code null}.
     */
    protected XmlBackupConfiguration2(Uri uri, boolean encrypt) throws NullPointerException {
        super(uri, encrypt);
    }

}
