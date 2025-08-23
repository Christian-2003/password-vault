package de.christian2003.passwordvault.domain.entry

import java.time.LocalDateTime
import kotlin.uuid.Uuid

/**
 * Detail stores account details (e.g. email, password, address, PIN, ...) for an entry.
 */
class Detail(

    /**
     * UUID for the detail.
     */
    var id: Uuid = Uuid.random(),

    /**
     * UUID of the entry, for which the detail is created.
     */
    var entry: Uuid,

    /**
     * Name of the detail.
     */
    var name: String,

    /**
     * Content of the detail.
     */
    var content: String,

    /**
     * Type of the detail.
     */
    var type: DetailType = DetailType.TEXT,

    /**
     * Icon of the detail. This can be null. In this case, the default icon of the detail type is
     * used.
     */
    var icon: DetailIcon? = null,

    /**
     * Whether the detail content is obfuscated.
     */
    var isObfuscated: Boolean = false,

    /**
     * Whether the detail content is visible by default or hidden beneath the "Show more details"
     * button.
     */
    var isVisible: Boolean = true,

    /**
     * Date time on which the detail was created. This is for statistical purposes.
     */
    val created: LocalDateTime = LocalDateTime.now(),

    /**
     * Date time on which the detail was edited the last time. This is for statistical purposes.
     */
    var edited: LocalDateTime = LocalDateTime.now()

) {

    /**
     * Returns the hash code for the detail. This is identical to the hash code of the ID of the
     * detail.
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
        return other is Detail && other.id == id
    }

}
