package de.passwordvault.backend.entry;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;
import de.passwordvault.R;
import de.passwordvault.backend.security.encryption.Encryptable;
import de.passwordvault.backend.security.encryption.EncryptionException;


/**
 * Class models a detail which can contain all types of detailed information for an entry.
 *
 * @author  Christian-2003
 * @version 2.1.0
 */
public class Detail {

    /**
     * Constant stores the type for undefined details.
     */
    public static final transient int TYPE_UNDEFINED = -1;

    /**
     * Constant stores the type for text.
     */
    public static final transient int TYPE_TEXT = 0;

    /**
     * Constant stores the type for numbers.
     */
    public static final transient int TYPE_NUMBER = 1;

    /**
     * Constant stores the type for security questions.
     */
    public static final transient int TYPE_SECURITY_QUESTION = 2;

    /**
     * Constant stores the type for addresses.
     */
    public static final transient int TYPE_ADDRESS = 3;

    /**
     * Constant stores the type for dates.
     */
    public static final transient int TYPE_DATE = 4;

    /**
     * Constant stores the type for e-mail addresses.
     */
    public static final transient int TYPE_EMAIL = 5;

    /**
     * Constant stores the type for passwords.
     */
    public static final transient byte TYPE_PASSWORD = 6;

    /**
     * Constant stores the type for URLs.
     */
    public static final transient int TYPE_URL = 7;


    /**
     * Attribute stores type 4 UUID of the detail.
     */
    private String uuid;

    /**
     * Attribute stores name of the detail.
     */
    private String name;

    /**
     * Attribute stores content of the detail.
     */
    private String content;

    /**
     * Attribute stores the date on which the detail was created.
     */
    private Calendar created;

    /**
     * Attribute stores the date on which the detail was changed the last time.
     */
    private Calendar changed;

    /**
     * Attribute stores the type of the detail.
     * The type defines the format of the content. This is used to enhance the way that the detail
     * is later displayed within the frontend.
     */
    private int type;

    /**
     * Attribute stores whether the detail shall be visible by default.
     */
    private boolean visible;

    /**
     * Attribute stores whether the details content shall be obfuscated when displayed. This might
     * be used to display passwords as '******' instead of 'abc123'.
     */
    private boolean obfuscated;

    /**
     * Attribute stores whether the detail shall be encrypted when stored on persistent memory.
     */
    private boolean encrypted;


    /**
     * Constructor instantiates a new Detail with a random type 4 UUID, and no contents.
     */
    public Detail() {
        uuid = UUID.randomUUID().toString();
        name = "";
        content = "";
        created = Calendar.getInstance();
        changed = created;
        type = TYPE_UNDEFINED;
        visible = true;
        obfuscated = false;
        encrypted = false;
    }

    /**
     * Constructor instantiates a new Detail and copies the attributes of the passed Detail to this
     * instance.
     *
     * @param detail    Detail whose values shall be copied to this instance.
     */
    public Detail(Detail detail) {
        if (detail == null) {
            throw new NullPointerException();
        }
        copyAttributesFromDetail(detail);
    }

    /**
     * Constructor instantiates a new Detail with the specified arguments.
     *
     * @param uuid      Type 4 UUID for the detail.
     * @param name      Name for the detail.
     * @param content   Content of the detail.
     * @param created   Date on which the detail was created.
     * @param changed   Date on which the detail was changed the last time.
     * @param type      Type for the detail.
     */
    public Detail(String uuid, String name, String content, Calendar created, Calendar changed, int type) {
        this.uuid = uuid;
        this.name = name;
        this.created = created;
        this.changed = changed;
        this.type = type;
        visible = true;
        obfuscated = false;
        encrypted = false;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public Calendar getChanged() {
        return changed;
    }

    public void setChanged(Calendar changed) {
        this.changed = changed;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isObfuscated() {
        return obfuscated;
    }

    public void setObfuscated(boolean obfuscated) {
        this.obfuscated = obfuscated;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }


    /**
     * Method tests whether the UUID of the passed detail is identical to the UUID of this detail.
     *
     * @param obj   Detail whose UUID shall be compared to the UUID of this detail.
     * @return      Whether both UUIDs are identical.
     */
    public boolean equals(Object obj) {
        if (obj instanceof Detail) {
            Detail detail = (Detail)obj;
            if (detail.getUuid().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generates a hash for this detail based on the UUID.
     *
     * @return  Generated hash for the detail.
     */
    public int hashCode() {
        return Objects.hash(uuid);
    }


    /**
     * Method converts this {@link #Detail} into a JSON string representation. If {@link #encrypted}
     * is set to {@code true}, the {@link #name} and {@link #content} are encrypted using the
     * passed encryption algorithm.
     *
     * @param algorithm             Algorithm with which to encrypt the data if necessary.
     * @return                      JSON representation of this instance.
     * @throws EncryptionException  The instance could not be encrypted.
     * @throws NullPointerException If the passed algorithm is {@code null} and {@link #encrypted}
     *                              is set to {@code true}.
     */
    public String toJson(Encryptable algorithm) throws EncryptionException, NullPointerException {
        String decryptedContent = content;
        String decryptedName = name;
        if (encrypted) {
            if (algorithm == null) {
                throw new NullPointerException("Null is invalid encryption algorithm");
            }
            content = algorithm.encrypt(content);
            name = algorithm.encrypt(name);
        }

        Gson gson = new Gson();
        String json = gson.toJson(this, Detail.class);

        content = decryptedContent;
        name = decryptedName;

        return json;
    }


    /**
     * Method converts the passed JSON representation of a {@link Detail} (which can be generated
     * through {@link #toJson(Encryptable)}) into a detail and stores it's attributes within this
     * instance. If {@link #encrypted} is set to {@code true}, the {@link #name} and {@link #content}
     * will be decrypted using the specified algorithm.
     *
     * @param json                      JSON to be converted into a detail.
     * @param algorithm                 Algorithm with which to decrypt the data if necessary.
     * @throws EncryptionException      The represented detail could not be decrypted.
     * @throws IllegalArgumentException The passed JSON could not be parsed into a detail.
     * @throws NullPointerException     The passed JSON is {@code null} OR the passed algorithm is
     *                                  {@code null} while {@link #encrypted} is set to {@code true}.
     */
    public void fromJson(String json, Encryptable algorithm) throws EncryptionException, IllegalArgumentException, NullPointerException {
        if (json == null) {
            throw new NullPointerException("Null is invalid JSON");
        }
        Gson gson = new Gson();
        Detail detail = gson.fromJson(json, Detail.class);
        if (detail == null) {
            throw new IllegalArgumentException("Json " + json + " is invalid");
        }
        copyAttributesFromDetail(detail);
        if (encrypted) {
            if (algorithm == null) {
                throw new NullPointerException("Null is invalid encryption algorithm");
            }
            content = algorithm.decrypt(content);
            name = algorithm.decrypt(name);
        }
    }


    /**
     * Notifies this Detail that some of its data was changed. This will update the value of
     * {@linkplain #changed} to the current date and time.
     */
    public void notifyDataChange() {
        changed = Calendar.getInstance();
    }


    /**
     * Static method returns a {@linkplain String} array which contains a String representation
     * for the available types. The respective String representation is taken from the passed
     * {@linkplain Context}.
     * The index within the array corresponds to the respective type.
     *
     * @param context   Context from which to retrieve the strings.
     * @return          String-array containing the String representations for the Detail types.
     */
    public static String[] GET_TYPES(Context context) {
        if (context == null) {
            return new String[0];
        }
        String[] types = new String[8];

        types[TYPE_TEXT] = context.getString(R.string.detail_text);
        types[TYPE_NUMBER] = context.getString(R.string.detail_number);
        types[TYPE_SECURITY_QUESTION] = context.getString(R.string.detail_security_question);
        types[TYPE_ADDRESS] = context.getString(R.string.detail_address);
        types[TYPE_DATE] = context.getString(R.string.detail_date);
        types[TYPE_EMAIL] = context.getString(R.string.detail_email);
        types[TYPE_PASSWORD] = context.getString(R.string.detail_password);
        types[TYPE_URL] = context.getString(R.string.detail_url);

        return types;
    }


    /**
     * Method copies all attributes from the passed {@link Detail} to this instance.
     *
     * @param detail    Detail whose attributes shall be copied to this instance.
     */
    private void copyAttributesFromDetail(@NonNull Detail detail) {
        this.uuid = detail.getUuid();
        this.name = detail.getName();
        this.content = detail.getContent();
        this.created = detail.getCreated();
        this.changed = detail.getChanged();
        this.type = detail.getType();
        this.visible = detail.isVisible();
        this.obfuscated = detail.isObfuscated();
        this.encrypted = detail.isEncrypted();
    }

}
