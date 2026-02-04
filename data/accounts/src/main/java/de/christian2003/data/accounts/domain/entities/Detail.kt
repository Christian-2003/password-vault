package de.christian2003.data.accounts.domain.entities

import java.time.LocalDateTime
import kotlin.uuid.Uuid


/**
 * Domain entity models a detail.
 *
 * @param name      Name for the detail.
 * @param content   Content for the detail.
 * @param id        Type 4 UUID for the detail.
 * @param type      Type for the detail provides additional autofill info.
 * @param icon      Icon for the detail. Can be null.
 * @param metadata  Metadata for the detail.
 */
class Detail(
    name: String,
    content: String,
    val id: Uuid = Uuid.random(),
    type: DetailType = DetailType.TEXT,
    icon: DetailIcon? = null,
    metadata: DetailMetadata = DetailMetadata()
) {

    /**
     * Name for the detail. This is displayed to the user so that they can identify the detail.
     * Examples might be "Password", "Email address" or "Payment Credit Card".
     */
    var name: String = name
        set(value) {
            require(value.isNotBlank()) { "Detail name cannot be blank" }
            field = value
            metadata = metadata.copy(editedAt = LocalDateTime.now())
        }

    /**
     * Content for the detail. This stores the info that should be auto filled, such as a password,
     * email address or credit card number.
     */
    var content: String = content
        set(value) {
            require(value.isNotBlank()) { "Detail content cannot be blank" }
            field = value
            metadata = metadata.copy(editedAt = LocalDateTime.now())
        }

        /**
     * Type for the detail. This provides additional info for the autofill service.
     */
    var type: DetailType = type
        set(value) {
            field = value
            metadata = metadata.copy(editedAt = LocalDateTime.now())
        }

    /**
     * Icon for the detail. If no icon was set by the user explicitly, this is null. In such a case,
     * use the default icon of the detail type.
     */
    var icon: DetailIcon? = icon
        set(value) {
            field = value
            metadata = metadata.copy(editedAt = LocalDateTime.now())
        }

    /**
     * Metadata for the detail.
     */
    var metadata: DetailMetadata = metadata


    /**
     * Initializes the detail instance.
     */
    init {
        require(id != Uuid.NIL) { "Detail ID cannot be Nil-UUID" }
        this.name = name
        this.content = content
        this.type = type
        this.icon = icon
        this.metadata = metadata
    }


    /**
     * Returns the hash code for the detail. This is identical to the hash code of the ID of the
     * entry.
     *
     * @return  Hash code for the detail.
     */
    override fun hashCode(): Int {
        return id.hashCode()
    }


    /**
     * Tests whether the object passed is equal to this detail, based on their IDs.
     *
     * @param other Other object to test.
     * @return      Whether the IDs of both objects are identical.
     */
    override fun equals(other: Any?): Boolean {
        return (other is Detail) && (other.id == id)
    }


    /**
     * Converts this detail instance into a string format. This string format can be used for
     * debugging and logging.
     *
     * @return  String representation of this detail instance.
     */
    override fun toString(): String {
        return "[$id] [$name]"
    }

}
